#!/usr/bin/env python3
"""Generate/check the evaluator-only 30-run schedule. Never launches an agent or VM."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import subprocess

HERE = Path(__file__).resolve().parent
ROOT = HERE.parents[2]
CONDITIONS = ["without-skill", "with-skill"]


def digest(raw):
    return hashlib.sha256(raw).hexdigest()


def require(value, message):
    if not value:
        raise ValueError(message)


def schedule_rows(tasks, repetitions, seed):
    require(repetitions == 3, "Main comparison requires exactly three repetitions per condition")
    require([t["id"] for t in tasks] == ["T01", "T02", "T03", "T04", "T05"], "Expected five unique tasks")
    rows = []
    for task in tasks:
        require(task["wallTimeSeconds"] == (900 if task["id"] in ["T01", "T02", "T03"] else 1200),
                "Unexpected per-task budget")
        for repetition in range(1, repetitions + 1):
            for condition in CONDITIONS:
                key = f"{seed}:{task['id']}:{repetition}:{condition}"
                rows.append({"runId": f"{task['id']}-r{repetition}-{condition}", "taskId": task["id"],
                             "repetition": repetition, "condition": condition, "orderKeySha256": digest(key.encode()),
                             "wallTimeSeconds": task["wallTimeSeconds"], "requiredSuites": task["requiredSuites"]})
    rows.sort(key=lambda r: r["orderKeySha256"])
    return [{"orderIndex": i, **row} for i, row in enumerate(rows, 1)]


def build(root=ROOT):
    here = root / "tools/agent-harness/eval"
    raw = (here / "tasks.json").read_bytes()
    manifest = json.loads(raw)
    require(manifest["conditions"] == CONDITIONS, "Unexpected comparison conditions")
    baseline = manifest["frozenCommit"]
    require(re.fullmatch(r"[0-9a-f]{40}", baseline), "Expected full frozen commit")

    def frozen(path):
        require(not Path(path).is_absolute() and ".." not in Path(path).parts, "Unsafe frozen path")
        return subprocess.check_output(["git", "show", baseline + ":" + path], cwd=root)

    reference = json.loads(frozen("tools/agent-harness/eval/tasks.json"))
    require(manifest["tasks"] == reference["tasks"], "Task definitions drifted from the baseline")
    lock = json.loads((here / "integration-pilot-lock.json").read_text())
    pins = {}
    for key, relative in {
        "profileSha256": "codex-vm-oauth-config.toml.template", "kitSha256": "docker-oauth-kit/spec.yaml",
        "watchdogSha256": "vm_job.py", "emptySshAgentSha256": "empty_ssh_agent.py",
    }.items():
        pins[key] = digest((here / relative).read_bytes())
        require(pins[key] == lock[key], "Verified runtime pin drifted: " + key)
    common = frozen("tools/agent-harness/eval/prompts/common.md")
    tasks = []
    for task in manifest["tasks"]:
        source = frozen(task["source"])
        before, after = task["mutation"]["before"].encode(), task["mutation"]["after"].encode()
        require(source.count(before) == 1, "Mutation anchor drift: " + task["id"])
        prompt = common + b"\n" + frozen("tools/agent-harness/eval/" + task["promptFile"])
        oracle = frozen(task["oracle"]["testFile"])
        for method in task["oracle"]["mustFailWhenSeeded"]:
            require(("void " + method + "(").encode() in oracle, "Oracle method missing")
            require(method.encode() not in prompt, "Oracle leaked into prompt")
        tasks.append({"taskId": task["id"], "source": task["source"], "healthySourceSha256": digest(source),
                      "regressionSourceSha256": digest(source.replace(before, after)), "promptSha256": digest(prompt),
                      "oracleFileSha256": digest(oracle), "requiredSuites": task["requiredSuites"]})
    rows = schedule_rows(manifest["tasks"], manifest["main"]["repetitionsPerCondition"], manifest["main"]["orderSeed"])
    require(len(rows) == 30 and sum(r["wallTimeSeconds"] for r in rows) == 30600, "Main budget mismatch")
    return {"schemaVersion": 1, "status": "unapproved-schedule-only", "evaluatorOnly": True,
            "baselineCommit": baseline, "taskManifestSha256": digest(raw), "orderSeed": manifest["main"]["orderSeed"],
            "agent": {"model": lock["model"], "reasoning": lock["reasoning"], "clientVersion": lock["clientVersion"], **pins},
            "maximumAgentExecutions": 30, "maximumAgentMinutes": 510, "outerRetries": 0,
            "maximumConcurrentAgents": 1, "stopOnRequiredAuthOrEnvironmentFailure": True,
            "authorizedAgentExecutions": 0, "agentExecutionsStarted": 0, "experimentReady": False,
            "remainingGates": ["Freeze complete readable input inventories and fresh-run VM lifecycle",
                               "Verify all five task red/green suites under the main runtime",
                               "Resolve or explicitly bound host OAuth rotation reliability",
                               "Confirm review protocol and finish human/model-review distinction",
                               "Obtain explicit source-transfer and bounded main-execution authorization"],
            "tasks": tasks, "runs": rows}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    for name in ["generate", "validate"]:
        command = commands.add_parser(name)
        command.add_argument("--schedule", type=Path, required=True)
    args = parser.parse_args()
    try:
        expected = build()
        if args.command == "generate":
            with args.schedule.open("x") as stream:
                json.dump(expected, stream, ensure_ascii=False, indent=2)
                stream.write("\n")
        else:
            require(json.loads(args.schedule.read_text()) == expected, "Schedule or frozen inputs changed")
        print(json.dumps({"status": "main-schedule-" + ("generated" if args.command == "generate" else "valid"),
                          "maximumAgentExecutions": 30, "maximumAgentMinutes": 510,
                          "authorizedAgentExecutions": 0, "agentExecutionsStarted": 0,
                          "experimentReady": False}, indent=2))
    except (ValueError, KeyError, OSError, subprocess.CalledProcessError) as error:
        parser.exit(1, f"main schedule error: {error}\n")


if __name__ == "__main__":
    main()
