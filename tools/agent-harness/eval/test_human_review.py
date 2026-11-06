import copy
import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("human_review", Path(__file__).with_name("human-review.py"))
review = importlib.util.module_from_spec(spec)
spec.loader.exec_module(review)


class HumanReviewTest(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.evidence = self.root / "raw"
        self.evidence.mkdir()
        self.result_path = self.root / "result.json"
        self.out = self.root / "packet"
        self.make_evidence()

    def write(self, path, text):
        path.parent.mkdir(parents=True, exist_ok=True)
        path.write_text(text)

    def make_evidence(self):
        counts = {"tests": 1, "failures": 0, "errors": 0, "skipped": 0}
        xml = '<testsuite name="Suite" tests="1" failures="0" errors="0" skipped="0"><testcase name="test()" classname="Suite"/></testsuite>'
        rows, audits, grades, ledger = [], [], [], []
        for index, condition in enumerate(("without-skill", "with-skill"), 1):
            run = f"run-{index}"
            source = f"// candidate {index}\n"
            diff = f"+{source}"
            source_hash, diff_hash = review.digest(source.encode()), review.digest(diff.encode())
            added = "src/test/java/NewTest.java"
            row = {"runId": run, "condition": condition, "candidateSourceSha256": source_hash,
                   "cliTotals": counts, "externalUnit": counts, "before": counts, "addedTestsAfter": counts,
                   "cliExitCode": 0, "status": "completed", "scopeValid": True,
                   "changedTrackedFiles": ["src/main/java/Product.java"], "addedFiles": [added],
                   "finalTrackedDiffMatched": True}
            rows.append(row)
            audits.append({"runId": run, "report": f"{run}/build-evidence/cli/report.json", "cliTotals": counts, "cliCommandEvent": "item_1"})
            grades.append({"runId": run, "candidateSourceSha256": source_hash, "totals": counts,
                           "finalBytesMatched": True, "originalTestsUnchanged": True})
            ledger.append({"runId": run, "finalProductDiffSha256": diff_hash})
            report = {"suite": "takenlecture-unit", "validationScope": "unit", "status": "passed",
                      "junit": {"totals": counts}, "problems": [], "limitations": ["mock only"],
                      "execution": {"exitCode": 0, "timedOut": False, "offline": True},
                      "codeBefore": {"trackedDiffSha256": diff_hash}, "codeAfter": {"trackedDiffSha256": diff_hash}}
            self.write(self.evidence / run / "candidate.java", source)
            self.write(self.evidence / run / "candidate.diff", diff)
            self.write(self.evidence / run / "added" / added, "// synthetic regression test\n")
            self.write(self.evidence / run / "final.txt", f"{condition} mju-verify {run} case-{index}\nUnit passed; DB untested. [log](/private/tmp/case-{index}/build/report.json)\n")
            self.write(self.evidence / run / "events.jsonl", json.dumps({"type": "item.completed", "item": {
                "type": "command_execution", "id": "item_1", "command": "node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline", "exit_code": 0}}))
            for directory in (f"{run}/build-evidence/cli", f"grade-{run}/verification"):
                self.write(self.evidence / directory / "report.json", json.dumps(report))
                self.write(self.evidence / directory / "junit/TEST-Suite.xml", xml)
            for stage in ("before", "after"):
                self.write(self.evidence / run / f"build-evidence/{stage}/TEST-Suite.xml", xml)
        review.dump(self.result_path, {"pilotVersion": 2, "taskId": "T01", "agentRunsExecuted": 2, "results": rows})
        for name, data in (("verification-audit.json", audits), ("external-grades.json", grades), ("run-ledger.json", ledger)):
            review.dump(self.evidence / name, data)

    def build(self):
        review.generate(self.evidence, self.result_path, self.out)
        return self.out / "reviewer"

    def completed_form(self, packet):
        form = review.read_json(packet / "scores.json")
        form["reviewer"] = {"name": "Synthetic test only", "completedAt": "2026-11-02T12:00:00+09:00",
                            "priorExposure": "test fixture", "humanAttestation": True}
        for row in form["reviews"]:
            for key in review.METRICS:
                row[key] = {"value": 2 if key == "verificationScore" else False,
                            "reason": "Synthetic reason", "evidence": [{"path": row["candidateId"] + "/final.md",
                                                                          "quote": "Unit passed; DB untested."}]}
        return form

    def check_form(self, packet, form):
        path = self.root / "completed.json"
        review.dump(path, form)
        return review.validate(packet, path, self.out / "evaluator-map.json")

    def test_packet_masks_identifiers_keeps_behavior_and_stays_ungraded(self):
        packet = self.build()
        for p in packet.rglob("*"):
            if p.is_file():
                self.assertNotRegex(p.read_text(), r"with(?:out)?-skill|mju-verify|\b(?:run|case)-[12]\b|/private/tmp/")
        finals = list(packet.glob("*/final.md"))
        self.assertEqual(2, len(finals))
        self.assertTrue(all("Unit passed; DB untested." in p.read_text() for p in finals))
        with self.assertRaisesRegex(ValueError, "attestation"):
            review.validate(packet, packet / "scores.json", self.out / "evaluator-map.json")
        with self.assertRaises(FileExistsError):
            review.generate(self.evidence, self.result_path, self.out)

    def test_mismatched_evidence_fails_before_output(self):
        path = self.evidence / "run-1/candidate.java"
        path.write_text("changed")
        with self.assertRaisesRegex(ValueError, "fingerprint"):
            self.build()
        self.assertFalse(self.out.exists())

    def test_score_form_rejects_missing_wrong_and_cross_candidate_evidence(self):
        packet = self.build()
        form = self.completed_form(packet)
        self.assertEqual("human-form-valid", self.check_form(packet, form)["status"])
        mutations = [
            lambda f: f.update(packetId="wrong"),
            lambda f: f["reviews"].pop(),
            lambda f: f["reviews"][0]["taskPassed"].update(reason=""),
            lambda f: f["reviews"][0]["verificationScore"].update(value=True),
            lambda f: f["reviews"][0]["taskPassed"].update(value=1),
            lambda f: f["reviews"][0]["taskPassed"].update(evidence=[]),
            lambda f: f["reviews"][0]["taskPassed"]["evidence"][0].update(quote="invented quote"),
            lambda f: f["reviews"][0]["taskPassed"]["evidence"][0].update(path="../evaluator-map.json"),
            lambda f: f["reviews"][0]["taskPassed"]["evidence"][0].update(path=f["reviews"][1]["candidateId"] + "/final.md"),
        ]
        for mutate in mutations:
            changed = copy.deepcopy(form)
            mutate(changed)
            with self.subTest(mutation=mutations.index(mutate)), self.assertRaises(ValueError):
                self.check_form(packet, changed)
        form["reviews"][0]["taskPassed"]["value"] = None
        self.assertEqual(1, self.check_form(packet, form)["unresolvedJudgments"])

    def test_modified_packet_and_manifest_are_rejected(self):
        packet = self.build()
        form = self.completed_form(packet)
        final = packet / form["reviews"][0]["candidateId"] / "final.md"
        final.write_text(final.read_text() + "forged evidence")
        with self.assertRaisesRegex(ValueError, "evidence changed"):
            self.check_form(packet, form)
        manifest = review.read_json(packet / "manifest.json")
        manifest["files"][str(final.relative_to(packet))] = review.digest(final.read_bytes())
        review.dump(packet / "manifest.json", manifest)
        with self.assertRaisesRegex(ValueError, "manifest changed"):
            self.check_form(packet, form)

    def test_human_validator_rejects_explicit_model_actor(self):
        packet = self.build()
        form = self.completed_form(packet)
        form["reviewer"]["kind"] = "model"
        with self.assertRaisesRegex(ValueError, "model reviewer cannot"):
            self.check_form(packet, form)

    def test_junit_total_mismatch_is_rejected(self):
        xml = self.evidence / "run-1/build-evidence/before/TEST-Suite.xml"
        xml.write_text(xml.read_text().replace('tests="1"', 'tests="2"'))
        with self.assertRaisesRegex(ValueError, "counters disagree"):
            self.build()

    def test_other_candidates_passing_external_report_is_rejected(self):
        first = self.evidence / "grade-run-1/verification/report.json"
        second = self.evidence / "grade-run-2/verification/report.json"
        second.write_bytes(first.read_bytes())
        with self.assertRaisesRegex(ValueError, "external grader verified a different diff"):
            self.build()
        self.assertFalse(self.out.exists())


if __name__ == "__main__":
    unittest.main()
