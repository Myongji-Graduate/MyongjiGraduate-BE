#!/usr/bin/env python3
"""Prepare/validate model-only review and a human sampling queue. Does not call a model."""
import argparse
import importlib.util
import json
from pathlib import Path
import secrets

HERE = Path(__file__).resolve().parent
spec = importlib.util.spec_from_file_location("review_common", HERE / "human-review.py")
common = importlib.util.module_from_spec(spec)
spec.loader.exec_module(common)


def prepare(evidence, result_path, out):
    common.require(not out.exists(), "output already exists")
    common.prepare_t04(evidence, result_path, out / "source-registration")
    registered = out / "source-registration"
    source = registered / "reviewer"
    original = common.read_json(source / "manifest.json")
    mapping = common.read_json(registered / "evaluator-map.json")
    packet = out / "reviewer"
    packet.mkdir()
    files = {}
    for name in original["files"]:
        if name == "README.md":
            continue
        content = common.local(source, name).read_bytes()
        target = packet / name
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(content)
        files[name] = common.digest(content)
    rubric = (HERE / "model-review-rubric.md").read_bytes()
    (packet / "rubric.md").write_bytes(rubric)
    files["rubric.md"] = common.digest(rubric)
    manifest = {"schemaVersion": 1, "packetId": secrets.token_hex(16), "kind": "model-review",
                "taskId": "T04", "candidates": original["candidates"], "files": files}
    common.dump(packet / "manifest.json", manifest)
    common.dump(out / "evaluator-map.json", {
        "packetId": manifest["packetId"], "mapping": mapping["mapping"],
        "manifestSha256": common.digest((packet / "manifest.json").read_bytes()),
        "sourcePreservationSha256": mapping["sourcePreservationSha256"],
        "sourceResultSha256": mapping["sourceResultSha256"],
        "sourceRegistrationManifestSha256": mapping["manifestSha256"]})
    form = {"schemaVersion": 1, "packetId": manifest["packetId"],
            "reviewer": {"kind": "model", "modelId": None, "humanAttestation": False},
            "reviews": [{"candidateId": candidate,
                         **{key: {"value": None, "reason": "", "evidence": []} for key in common.METRICS}}
                        for candidate in manifest["candidates"]]}
    common.dump(out / "model-score-template.json", form)
    return {"status": "model-review-prepared-unscored", "reviewerDirectory": str(packet),
            "modelCallsExecutedByTool": 0, "codingAgentExecutionsStarted": 0}


def validate(packet, scores_path, map_path):
    manifest = common.validate_packet(packet, map_path)
    common.require(manifest.get("kind") == "model-review", "expected model-review packet")
    actual = set()
    for path in packet.rglob("*"):
        common.require(not path.is_symlink(), "model packet symlink is not supported")
        if path.is_file():actual.add(str(path.relative_to(packet)))
        else:common.require(path.is_dir(), "model packet special file is not supported")
    common.require(actual == set(manifest["files"]) | {"manifest.json"}, "model packet file set changed")
    form = common.read_json(scores_path)
    common.require(set(form) == {"schemaVersion", "packetId", "reviewer", "reviews"}, "unexpected model form fields")
    common.require(form["schemaVersion"] == 1 and form["packetId"] == manifest["packetId"], "wrong model score packet")
    reviewer = form["reviewer"]
    common.require(set(reviewer) == {"kind", "modelId", "humanAttestation"}, "unexpected reviewer fields")
    common.require(reviewer.get("kind") == "model" and reviewer.get("humanAttestation") is False,
                   "model reviews cannot attest human scoring")
    common.require(reviewer.get("modelId") is None or isinstance(reviewer["modelId"], str), "invalid model identity")
    unresolved = common.validate_metrics(packet, manifest, form["reviews"])
    for row in form["reviews"]:
        common.require(set(row) == {"candidateId", *common.METRICS}, "unexpected model review fields")
        for key in common.METRICS:
            common.require(set(row[key]) == {"value", "reason", "evidence"}, "unexpected model metric fields")
            common.require(all(set(c) == {"path", "quote"} for c in row[key]["evidence"]), "unexpected citation fields")
        evidence = common.read_json(common.local(packet, row["candidateId"] + "/evidence.json"))
        grade = evidence["externalGrading"]
        objective_pass = (not evidence["forbiddenChangedPaths"] and grade["candidateBytesMatched"] is True
                          and grade["originalTestsCliConfigUnchanged"] is True
                          and grade.get("gradeStatus") == "original-required-suites-passed"
                          and len(grade["suites"]) == 2
                          and {r["suite"] for r in grade["suites"]} == {"takenlecture-unit", "takenlecture-integration"}
                          and all(r["status"] == "passed" and r["totals"]["tests"] > 0
                                  and all(r["totals"][k] == 0 for k in ["failures", "errors", "skipped"])
                                  for r in grade["suites"]))
        common.require(row["taskPassed"]["value"] is not True or objective_pass,
                       "model pass contradicts required objective evidence")
    return {"status": "model-form-valid", "reviews": len(form["reviews"]), "unresolvedJudgments": unresolved,
            "reviewerKind": "model", "humanScoringCompleted": False, "humanCalibrationCompleted": False,
            "qualitativeJudgmentsVerified": False, "sourceResultsUpdated": False}


def queue(form):
    rows = form["reviews"]
    common.require(bool(rows), "no reviewed candidates")
    sample = sorted(row["candidateId"] for row in rows)[0]
    output = []
    for row in rows:
        reasons = []
        if any(row[key]["value"] is None for key in common.METRICS):
            reasons.append("uncertain-model-judgment")
        if (row["taskPassed"]["value"] is False or row["falseCompletion"]["value"] is True
                or row["scopeOverclaim"]["value"] is True
                or row["verificationScore"]["value"] in (0, 1)):
            reasons.append("model-reported-failure-or-gap")
        if row["candidateId"] == sample:
            reasons.append("initial-calibration-sample")
        output.append({"candidateId": row["candidateId"], "suggestedHumanReview": bool(reasons), "reasons": reasons})
    return {"status": "human-review-queue-proposed", "policy": "all uncertainties/gaps plus one initial sample",
            "humanCalibrationCompleted": False, "blocksDevelopmentCli": False, "candidates": output}


def prepare_human_sample(packet, scores_path, map_path, out):
    validate(packet, scores_path, map_path)
    common.require(not out.exists(), "output already exists")
    proposed = queue(common.read_json(scores_path))
    selected = [r["candidateId"] for r in proposed["candidates"] if r["suggestedHumanReview"]]
    mapping = common.read_json(map_path)
    registered = packet.parent / "source-registration"
    source = registered / "reviewer"
    original = common.validate_packet(source, registered / "evaluator-map.json")
    common.require(common.digest((source / "manifest.json").read_bytes()) == mapping["sourceRegistrationManifestSha256"],
                   "source registration changed")
    model_manifest = common.read_json(packet / "manifest.json")
    files = {}
    for name, expected in original["files"].items():
        if name not in ["README.md", "task.md"] and name.split("/")[0] not in selected:
            continue
        if name != "README.md":
            common.require(model_manifest["files"].get(name) == expected, "sample evidence differs from model input")
        files[name] = common.local(source, name).read_bytes()
    manifest = {"schemaVersion": 1, "packetId": secrets.token_hex(16), "kind": "human-review",
                "taskId": "T04", "candidates": selected,
                "files": {name: common.digest(raw) for name, raw in files.items()}}
    form = {"schemaVersion": 1, "packetId": manifest["packetId"], "reviewer": {
        "kind": "human", "name": "", "completedAt": "", "priorExposure": "", "humanAttestation": False},
        "reviews": [{"candidateId": candidate, **{
            key: {"value": None, "reason": "", "evidence": []} for key in common.METRICS}} for candidate in selected]}
    out.mkdir(parents=True)
    reviewer = out / "reviewer"; reviewer.mkdir()
    for name, raw in files.items():
        target = reviewer / name; target.parent.mkdir(parents=True, exist_ok=True); target.write_bytes(raw)
    common.dump(reviewer / "manifest.json", manifest)
    common.dump(reviewer / "scores.json", form)
    common.dump(out / "evaluator-map.json", {
        "packetId": manifest["packetId"], "mapping": {c: mapping["mapping"][c] for c in selected},
        "manifestSha256": common.digest((reviewer / "manifest.json").read_bytes()),
        "sourceModelManifestSha256": mapping["manifestSha256"],
        "sourceModelScoresSha256": common.digest(scores_path.read_bytes()),
        "selectionPolicy": proposed["policy"], "modelJudgmentsIncludedInReviewerPacket": False})
    return {"status": "human-sample-prepared-ungraded", "candidates": len(selected),
            "reviewerDirectory": str(reviewer), "humanCalibrationCompleted": False, "sourceResultsUpdated": False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    create = commands.add_parser("prepare")
    create.add_argument("--evidence", type=Path, required=True)
    create.add_argument("--result", type=Path, default=HERE / "integration-pilot-result.json")
    create.add_argument("--out", type=Path, required=True)
    for name in ["validate", "queue", "human-sample"]:
        command = commands.add_parser(name)
        command.add_argument("--packet", type=Path, required=True)
        command.add_argument("--scores", type=Path, required=True)
        command.add_argument("--map", type=Path, required=True)
        if name in ["queue", "human-sample"]:command.add_argument("--out", type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.command == "prepare":
            output = prepare(args.evidence, args.result, args.out)
        elif args.command == "human-sample":
            output = prepare_human_sample(args.packet, args.scores, args.map, args.out)
        else:
            output = validate(args.packet, args.scores, args.map)
            if args.command == "queue":
                output = queue(common.read_json(args.scores))
                with args.out.open("x") as stream:
                    json.dump(output, stream, ensure_ascii=False, indent=2)
                    stream.write("\n")
        print(json.dumps(output, ensure_ascii=False, indent=2))
    except (ValueError, KeyError, OSError, TypeError) as error:
        parser.exit(1, f"model review error: {error}\n")


if __name__ == "__main__":main()
