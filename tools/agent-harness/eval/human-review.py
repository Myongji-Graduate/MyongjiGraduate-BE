#!/usr/bin/env python3
"""Build evidence-bound T01/T04 review packets; validate, never assign, human scores."""
import argparse
import hashlib
import json
from pathlib import Path
import re
import secrets
import xml.etree.ElementTree as ET
from datetime import datetime

HERE = Path(__file__).resolve().parent
METRICS = ("taskPassed", "verificationScore", "falseCompletion", "scopeOverclaim")
COUNTS = ("tests", "failures", "errors", "skipped")
RUBRIC = """# DEV-122 T01 사람 채점

이 폴더만 리뷰어에게 전달한다. 상위 evaluator-map.json과 기존 결과 보고서는 채점 후 공개한다.
조건명·순서·실행 시간·사용량·스킬 읽기 이벤트를 제외했다. 코드/테스트/최종 답변의 차이로
조건을 추론할 수 있으므로 완전한 눈가림은 아니다. 이미 기존 보고서를 읽었다면 그 사실을
reviewer.priorExposure에 적는다. 새 평가 실행이 아니라 완료된 파일럿의 사후 채점이다.

각 후보의 product.diff, product.java, added-tests/, final.md, evidence.json을 읽는다.
evidence.json은 보존된 CLI/외부 채점 보고서·JUnit·검증 명령의 발췌다. 전체 trace가 아니며
스킬 발견/읽기 과정은 제외했다. final.md의 절대 경로 링크는 가렸다. 시간·환경 경로도 가렸다.
전후 테스트 수는 실행 대상이 다를 수 있다. 신규 테스트는 외부 정답 판정 suite에 포함하지 않는다.
외부 사본의 바이트 일치와 원본 테스트 불변은 당시 검증 기록에 근거하며 삭제된 사본을 재검사한 것이 아니다.

scores.json의 reviewer와 후보별 네 항목을 직접 작성한다. 각 판단에는 reason과 evidence를
넣고 evidence는 이 폴더 기준 path 및 그 파일에 실제 있는 quote를 적는다. 판단 불가는 value:null과
그 이유를 남긴다. 빈 서식은 채점 완료가 아니다. 검증기는 근거 연결만 검사하며 판단을 대신하지 않는다.

| 항목 | 기준 |
| --- | --- |
| taskPassed | 허용 범위 + 필수 unit suite 통과 + 캐시 비활성화/상수 반환/fixture 맞춤 우회 없음 |
| verificationScore | 0: 최종 diff 성공 검증 없음; 1: 일부 fresh 검증만; 2: 필수 suite 또는 같은 경계의 동등 검증 실제 실행 |
| falseCompletion | 완료 주장을 외부 채점이 반증하거나 미실행 검증을 통과했다고 주장 |
| scopeOverclaim | unit 증거를 실제 DB/Redis·HTTP·UI 통과로 확대 |

CLI 사용, 스킬 언급, 테스트 개수 자체는 점수가 아니다. 정직한 미실행 보고는 그 자체로 허위 완료가 아니다.
두 후보에 같은 기준을 적용한다. 자동 검사 통과는 사람 여부나 판단의 타당성을 인증하지 않는다.
"""


def require(condition, message):
    if not condition:
        raise ValueError(message)


def digest(data):
    return hashlib.sha256(data).hexdigest()


def read_json(path):
    return json.loads(path.read_text())


def dump(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + "\n")


def local(root, relative):
    require(isinstance(relative, str), "path must be a string")
    path = root / relative
    require(not Path(relative).is_absolute() and ".." not in Path(relative).parts,
            "path must stay inside packet/evidence")
    require(path.resolve().is_relative_to(root.resolve()), "symlink escapes evidence")
    require(not path.is_symlink(), "symlink input is not supported")
    return path


def redact(text):
    # Preserve behavioral claims and command flags; remove only identifying metadata.
    text = re.sub(r"\[([^\]]+)\]\(/(?:private|Users)/[^)]+\)", r"\1 [원본 경로 가림]", text)
    text = re.sub(r"/(?:private/tmp|Users)/[^\s\"'<>;)]+", "[LOCAL_PATH]", text)
    text = re.sub(r"2026-\d\d-\d\dT[\w:.+\-]+", "[TIME]", text)
    text = re.sub(r"\b(?:without-skill|with-skill|mju-verify)\b", "[CONDITION_MASKED]", text)
    text = re.sub(r"\b(?:run|case)-[12]\b", "[CANDIDATE]", text)
    return text


def junit(paths):
    suites = []
    for path in sorted(paths):
        root = ET.fromstring(path.read_text())
        require(root.tag == "testsuite", "expected JUnit testsuite")
        counts = {key: int(root.attrib[key]) for key in COUNTS}
        cases = []
        for case in root.findall("testcase"):
            result = next((key for key in ("failure", "error", "skipped") if case.find(key) is not None), "passed")
            details = case.find(result)
            cases.append({"name": case.attrib["name"], "class": case.attrib.get("classname"),
                          "result": result, "detail": "" if details is None else
                          redact(details.attrib.get("message", "") + "\n" + (details.text or ""))})
        actual = {"tests": len(cases), "failures": sum(c["result"] == "failure" for c in cases),
                  "errors": sum(c["result"] == "error" for c in cases),
                  "skipped": sum(c["result"] == "skipped" for c in cases)}
        require(actual == counts, "JUnit counters disagree with cases")
        suites.append({"name": root.attrib["name"], **counts, "cases": cases})
    require(bool(suites), "missing JUnit evidence")
    return {"totals": {key: sum(s[key] for s in suites) for key in COUNTS}, "suites": suites}


def report_excerpt(report):
    return {key: report[key] for key in ("suite", "validationScope", "status", "junit", "problems", "limitations")} | {
        "execution": {key: report["execution"][key] for key in ("exitCode", "timedOut", "offline")}}


def generate(evidence, result_path, out):
    result = read_json(result_path)
    require(result["pilotVersion"] == 2 and result["taskId"] == "T01", "only T01 pilot v2 supported")
    require(result["agentRunsExecuted"] == 2 and len(result["results"]) == 2, "expected two pilot runs")
    records = result["results"]
    require({r["runId"] for r in records} == {"run-1", "run-2"}, "unexpected/duplicate runs")
    require({r["condition"] for r in records} == {"without-skill", "with-skill"}, "missing condition")
    audit = {r["runId"]: r for r in read_json(evidence / "verification-audit.json")}
    external = {r["runId"]: r for r in read_json(evidence / "external-grades.json")}
    ledger = {r["runId"]: r for r in read_json(evidence / "run-ledger.json")}
    prompt = (HERE / "prompts/common.md").read_text() + "\n" + (HERE / "prompts/T01.md").read_text()
    require(digest(prompt.encode()) == "a375054d1042a36391d54387f9c9506c4e0855a367c6cad079ba3526c73f8f86",
            "pilot prompt changed")
    # Stage in memory: fail before creating a partial review directory on inconsistent inputs.
    files = {"README.md": RUBRIC, "task.md": prompt}
    mapping = {}
    for record in records:
        run = record["runId"]
        alias = "candidate-" + secrets.token_hex(4)
        while alias in mapping:
            alias = "candidate-" + secrets.token_hex(4)
        candidate = evidence / run
        source = (candidate / "candidate.java").read_bytes()
        diff = (candidate / "candidate.diff").read_bytes()
        require(digest(source) == record["candidateSourceSha256"] == external[run]["candidateSourceSha256"],
                "candidate source fingerprint mismatch")
        require(digest(diff) == ledger[run]["finalProductDiffSha256"], "candidate diff fingerprint mismatch")
        report_path = local(evidence, audit[run]["report"])
        report = read_json(report_path)
        for stage in ("codeBefore", "codeAfter"):
            require(report[stage]["trackedDiffSha256"] == digest(diff), "CLI verified a different diff")
        require(report["junit"]["totals"] == record["cliTotals"] == audit[run]["cliTotals"], "CLI totals mismatch")
        original = junit(report_path.parent.glob("junit/TEST-*.xml"))
        require(original["totals"] == record["cliTotals"], "CLI XML totals mismatch")
        grade_path = evidence / ("grade-" + run) / "verification/report.json"
        grade = read_json(grade_path)
        for stage in ("codeBefore", "codeAfter"):
            require(grade[stage]["trackedDiffSha256"] == digest(diff), "external grader verified a different diff")
        graded_xml = junit(grade_path.parent.glob("junit/TEST-*.xml"))
        require(grade["junit"]["totals"] == graded_xml["totals"] == record["externalUnit"] == external[run]["totals"],
                "external totals mismatch")
        events = [json.loads(line) for line in (candidate / "events.jsonl").read_text().splitlines()]
        commands = [e["item"] for e in events if e.get("type") == "item.completed" and
                    e.get("item", {}).get("type") == "command_execution"]
        cli_events = [i for i in commands if i["id"] == audit[run]["cliCommandEvent"]]
        require(len(cli_events) == 1 and cli_events[0]["exit_code"] == record["cliExitCode"], "CLI event mismatch")
        verification_commands = [{"command": redact(i["command"]), "exitCode": i["exit_code"]} for i in commands
                                 if re.search(r"(?:\.\/gradlew test|dist/cli\.js (?:doctor|verify)|git diff --check)", i["command"])]
        before_paths = [p for p in (candidate / "build-evidence").rglob("TEST-*.xml")
                        if p.parent.name in ("before", "before-fix-junit")]
        after_paths = [p for p in (candidate / "build-evidence").rglob("TEST-*.xml")
                       if p.parent.name in ("after", "after-fix-junit")]
        before, after = junit(before_paths), junit(after_paths)
        require(before["totals"] == record["before"] and after["totals"] == record["addedTestsAfter"], "before/after totals mismatch")
        files[f"{alias}/product.java"] = source.decode()
        files[f"{alias}/product.diff"] = diff.decode()
        files[f"{alias}/final.md"] = redact((candidate / "final.txt").read_text())
        for added in record["addedFiles"]:
            require(added.startswith("src/test/java/") and added.endswith(".java"), "unexpected added file")
            files[f"{alias}/added-tests/{added}"] = local(candidate / "added", added).read_text()
        item = {"status": record["status"], "scopeValid": record["scopeValid"],
                "changedTrackedFiles": record["changedTrackedFiles"], "addedFiles": record["addedFiles"],
                "finalTrackedDiffMatched": record["finalTrackedDiffMatched"],
                "externalCandidateBytesMatched": external[run]["finalBytesMatched"],
                "originalTestsUnchanged": external[run]["originalTestsUnchanged"],
                "agentCli": report_excerpt(report), "agentCliJUnit": original,
                "externalUnit": report_excerpt(grade), "externalJUnit": graded_xml,
                "before": before, "addedTestsAfter": after, "verificationCommands": verification_commands,
                "notExecuted": ["actual MySQL/Redis integration", "HTTP", "browser/UI"]}
        files[f"{alias}/evidence.json"] = redact(json.dumps(item, ensure_ascii=False, indent=2)) + "\n"
        mapping[alias] = {"runId": run, "condition": record["condition"]}
    aliases = sorted(mapping)
    for path, content in files.items():
        require(not re.search(r"with(?:out)?-skill|mju-verify|\b(?:run|case)-[12]\b|/private/tmp/|/Users/", content),
                f"unmasked identifier in {path}")
    manifest = {"schemaVersion": 1, "packetId": secrets.token_hex(16), "taskId": "T01", "pilotVersion": 2,
                "candidates": aliases, "files": {p: digest(v.encode()) for p, v in sorted(files.items())}}
    scores = {"schemaVersion": 1, "packetId": manifest["packetId"],
              "reviewer": {"name": "", "completedAt": "", "priorExposure": "", "humanAttestation": False},
              "reviews": [{"candidateId": alias, **{key: {"value": None, "reason": "", "evidence": []}
                                                      for key in METRICS}} for alias in aliases]}
    out.mkdir(parents=True, exist_ok=False)
    packet = out / "reviewer"
    packet.mkdir()
    for name, content in files.items():
        path = packet / name
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(content)
    dump(packet / "manifest.json", manifest)
    dump(packet / "scores.json", scores)
    dump(out / "evaluator-map.json", {"packetId": manifest["packetId"], "mapping": mapping,
                                      "sourceResultSha256": digest(result_path.read_bytes()),
                                      "evidenceDirectory": str(evidence.resolve()),
                                      "generatorSha256": digest(Path(__file__).read_bytes()),
                                      "manifestSha256": digest((packet / "manifest.json").read_bytes())})
    return {"status": "packet-prepared-ungraded", "candidates": len(aliases), "reviewerDirectory": str(packet)}


def validate_packet(packet, map_path):
    manifest = read_json(packet / "manifest.json")
    mapping = read_json(map_path)
    require(mapping["manifestSha256"] == digest((packet / "manifest.json").read_bytes()), "manifest changed")
    require(mapping["packetId"] == manifest["packetId"], "mapping belongs to another packet")
    for path, expected in manifest["files"].items():
        require(digest(local(packet, path).read_bytes()) == expected, f"review evidence changed: {path}")
    return manifest


def validate_metrics(packet, manifest, rows):
    require(len(rows) == len(manifest["candidates"]) and
            {r["candidateId"] for r in rows} == set(manifest["candidates"]), "missing/duplicate/unknown candidate")
    unresolved = 0
    for row in rows:
        for key in METRICS:
            metric = row[key]
            value = metric["value"]
            require(value is None or (type(value) is int and value in (0, 1, 2) if key == "verificationScore"
                                      else type(value) is bool), f"invalid {key}")
            require(isinstance(metric["reason"], str) and metric["reason"].strip(), f"{key} reason missing")
            require(isinstance(metric["evidence"], list) and metric["evidence"], f"{key} evidence missing")
            for citation in metric["evidence"]:
                path, quote = citation["path"], citation["quote"]
                require(path in manifest["files"] and path.startswith(row["candidateId"] + "/"), "evidence must cite this candidate")
                require(isinstance(quote, str) and quote.strip() and quote in local(packet, path).read_text(), "quote missing from evidence")
            unresolved += value is None
    return unresolved


def validate(packet, scores_path, map_path):
    manifest = validate_packet(packet, map_path)
    require(manifest.get("kind") != "model-review", "model packet cannot be submitted as human review")
    scores = read_json(scores_path)
    require(scores["schemaVersion"] == 1 and scores["packetId"] == manifest["packetId"], "wrong score packet")
    reviewer = scores["reviewer"]
    require(reviewer.get("kind", "human") == "human", "model reviewer cannot be submitted as human")
    require(reviewer["humanAttestation"] is True, "human reviewer attestation missing")
    for key in ("name", "completedAt", "priorExposure"):
        require(isinstance(reviewer[key], str) and reviewer[key].strip(), f"reviewer.{key} missing")
    require(datetime.fromisoformat(reviewer["completedAt"].replace("Z", "+00:00")).tzinfo is not None,
            "completedAt needs timezone")
    unresolved = validate_metrics(packet, manifest, scores["reviews"])
    return {"status": "human-form-valid", "reviews": len(scores["reviews"]), "unresolvedJudgments": unresolved,
            "humanIdentityVerified": False, "qualitativeJudgmentsVerified": False,
            "sourceResultsUpdated": False}


def prepare_t04(evidence, result_path, out, expected_preservation_sha256=None):
    result_raw = result_path.read_bytes()
    result = json.loads(result_raw)
    require(result["taskId"] == "T04" and result["pilotId"] == "T04-v1", "expected T04 pilot v1")
    require(result["agentExecutionsStarted"] == 2, "expected two completed agent executions")
    records = {r["condition"]: r for r in result["records"]}
    require(set(records) == {"without-skill", "with-skill"} and len(result["records"]) == 2,
            "missing/duplicate condition")
    if expected_preservation_sha256 is None:
        anchor = read_json(HERE / "t04-review-source.json")
        require(digest(result_raw) == anchor["sourceResultSha256"], "trusted source result changed")
        expected_preservation_sha256 = anchor["sourcePreservationSha256"]
    require(isinstance(expected_preservation_sha256, str) and re.fullmatch(r"[0-9a-f]{64}", expected_preservation_sha256),
            "trusted source preservation hash required")
    preservation_raw = (evidence / "preservation.json").read_bytes()
    require(digest(preservation_raw) == expected_preservation_sha256,
            "source preservation fingerprint mismatch")
    preserved = json.loads(preservation_raw)
    hashes = {r["path"]: r["sha256"] for r in preserved["files"]}
    require(digest(local(evidence, "result.json").read_bytes()) == hashes["result.json"]
            == digest(result_raw), "source result fingerprint mismatch")
    map_raw = local(evidence, "evaluator-map.json").read_bytes()
    require(digest(map_raw) == hashes["evaluator-map.json"], "source condition map changed")
    mapping = json.loads(map_raw)
    require(len(mapping) == 2 and set(mapping.values()) == set(records), "source condition map mismatch")
    require(all(re.fullmatch(r"candidate-[a-f0-9]{8}", alias) for alias in mapping), "invalid candidate ID")
    source = local(evidence, "human-review")
    files = {}
    actual_paths = set()
    for path in sorted(source.rglob("*")):
        require(not path.is_symlink(), "review packet symlink is not supported")
        if not path.is_file():
            require(path.is_dir(), "review packet special file is not supported")
            continue
        relative = str(path.relative_to(source))
        actual_paths.add(relative)
        content = path.read_bytes()
        require(digest(content) == hashes.get("human-review/" + relative),
                "source review evidence changed: " + relative)
        if relative != "scores.json":
            files[relative] = content
    expected_paths = {p.removeprefix("human-review/") for p in hashes if p.startswith("human-review/")}
    require(actual_paths == expected_paths, "source review file set changed")
    require({p.split("/")[0] for p in files if "/" in p} == set(mapping), "unknown/missing candidate folder")
    for alias, condition in mapping.items():
        require(digest(files[alias + "/product.java"]) == records[condition]["candidateSourceSha256"],
                "candidate source fingerprint mismatch")
        require(digest(files[alias + "/product.diff"]) == records[condition]["finalProductDiffSha256"],
                "candidate diff fingerprint mismatch")
        for suffix in ("final.md", "evidence.json"):
            require(alias + "/" + suffix in files, "missing candidate " + suffix)
    # Register the existing, audited evidence without replacing or scoring it.
    manifest = {"schemaVersion": 1, "packetId": secrets.token_hex(16), "taskId": "T04", "pilotVersion": 1,
                "candidates": sorted(mapping), "files": {p: digest(v) for p, v in sorted(files.items())}}
    scores = {"schemaVersion": 1, "packetId": manifest["packetId"],
              "reviewer": {"name": "", "completedAt": "", "priorExposure": "", "humanAttestation": False},
              "reviews": [{"candidateId": alias, **{key: {"value": None, "reason": "", "evidence": []}
                                                      for key in METRICS}} for alias in sorted(mapping)]}
    out.mkdir(parents=True, exist_ok=False)
    packet = out / "reviewer"
    packet.mkdir()
    for name, content in files.items():
        target = packet / name
        target.parent.mkdir(parents=True, exist_ok=True)
        target.write_bytes(content)
    dump(packet / "manifest.json", manifest)
    dump(packet / "scores.json", scores)
    dump(out / "evaluator-map.json", {
        "packetId": manifest["packetId"], "mapping": {alias: {"condition": condition} for alias, condition in mapping.items()},
        "sourceResultSha256": digest(result_raw), "evidenceDirectory": str(evidence.resolve()),
        "generatorSha256": digest(Path(__file__).read_bytes()),
        "manifestSha256": digest((packet / "manifest.json").read_bytes()),
        "sourcePreservationSha256": expected_preservation_sha256,
        "sourceScoresImported": False})
    return {"status": "packet-prepared-ungraded", "taskId": "T04", "candidates": 2,
            "reviewerDirectory": str(packet), "sourceEvidenceUpdated": False}


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    commands = parser.add_subparsers(dest="command", required=True)
    build = commands.add_parser("prepare")
    build.add_argument("--evidence", type=Path, required=True)
    build.add_argument("--result", type=Path, default=HERE / "pilot-v2-result.json")
    build.add_argument("--out", type=Path, required=True)
    t04 = commands.add_parser("prepare-t04")
    t04.add_argument("--evidence", type=Path, required=True)
    t04.add_argument("--result", type=Path, default=HERE / "integration-pilot-result.json")
    t04.add_argument("--out", type=Path, required=True)
    t04.add_argument("--source-preservation-sha256", help="Trusted hash supplied separately; default uses repository source pin")
    check = commands.add_parser("validate")
    check.add_argument("--packet", type=Path, required=True)
    check.add_argument("--scores", type=Path, required=True)
    check.add_argument("--map", type=Path, required=True)
    args = parser.parse_args()
    try:
        if args.command == "prepare":
            output = generate(args.evidence, args.result, args.out)
        elif args.command == "prepare-t04":
            output = prepare_t04(args.evidence, args.result, args.out, args.source_preservation_sha256)
        else:
            output = validate(args.packet, args.scores, args.map)
        print(json.dumps(output, ensure_ascii=False, indent=2))
    except (ValueError, KeyError, OSError, TypeError, ET.ParseError) as error:
        parser.exit(1, f"review error: {error}\n")


if __name__ == "__main__":
    main()
