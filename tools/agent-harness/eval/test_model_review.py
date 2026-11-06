import copy
import importlib.util
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("model_review", Path(__file__).with_name("model-review.py"))
review = importlib.util.module_from_spec(spec)
spec.loader.exec_module(review)


class ModelReviewTest(unittest.TestCase):
    def setUp(self):
        temp = tempfile.TemporaryDirectory()
        self.addCleanup(temp.cleanup)
        self.root = Path(temp.name)
        self.packet = self.root / "reviewer"
        self.packet.mkdir()
        (self.packet / "task.md").write_text("Synthetic task fixture.\n")
        self.files = {}
        self.ids = ["candidate-11111111", "candidate-22222222"]
        for candidate in self.ids:
            folder = self.packet / candidate
            folder.mkdir()
            (folder / "final.md").write_text("Synthetic DB verified. HTTP/UI untested.\n")
            review.common.dump(folder / "evidence.json", {
                "forbiddenChangedPaths": [], "externalGrading": {
                    "candidateBytesMatched": True, "originalTestsCliConfigUnchanged": True,
                    "gradeStatus": "original-required-suites-passed",
                    "suites": [{"suite": suite, "status": "passed", "totals": {
                        "tests": count, "failures": 0, "errors": 0, "skipped": 0}}
                        for suite, count in [("takenlecture-unit", 21), ("takenlecture-integration", 31)]]}})
        self.reseal()
        self.form = {"schemaVersion": 1, "packetId": "synthetic", "reviewer": {
            "kind": "model", "modelId": None, "humanAttestation": False}, "reviews": []}
        for candidate in self.ids:
            self.form["reviews"].append({"candidateId": candidate, **{
                key: {"value": 2 if key == "verificationScore" else key == "taskPassed",
                      "reason": "Synthetic test only", "evidence": [{
                          "path": candidate + "/final.md", "quote": "HTTP/UI untested."}]}
                for key in review.common.METRICS}})

    def reseal(self):
        files = {str(p.relative_to(self.packet)): review.common.digest(p.read_bytes())
                 for p in self.packet.rglob("*") if p.is_file() and p.name != "manifest.json"}
        review.common.dump(self.packet / "manifest.json", {"schemaVersion": 1, "packetId": "synthetic",
            "kind": "model-review", "candidates": self.ids, "files": files})
        self.map = self.root / "map.json"
        review.common.dump(self.map, {"packetId": "synthetic", "manifestSha256":
                                     review.common.digest((self.packet / "manifest.json").read_bytes())})

    def check(self, form=None):
        scores = self.root / "model-scores.json"
        review.common.dump(scores, self.form if form is None else form)
        return review.validate(self.packet, scores, self.map)

    def test_model_review_does_not_become_human_or_verified_judgment(self):
        result = self.check()
        self.assertEqual("model-form-valid", result["status"])
        self.assertFalse(result["humanScoringCompleted"])
        self.assertFalse(result["humanCalibrationCompleted"])
        self.assertFalse(result["qualitativeJudgmentsVerified"])
        self.form["reviewer"]["humanAttestation"] = True
        with self.assertRaisesRegex(ValueError, "cannot attest human"):
            self.check()

    def test_model_packet_cannot_use_the_human_validator(self):
        form = copy.deepcopy(self.form)
        form["reviewer"].update(humanAttestation=True, name="synthetic model", completedAt="2026-11-03T12:00:00+09:00",
                                priorExposure="synthetic only")
        path = self.root / "wrong-human-form.json"
        review.common.dump(path, form)
        with self.assertRaisesRegex(ValueError, "model packet cannot be submitted as human"):
            review.common.validate(self.packet, path, self.map)

    def test_wrong_cross_candidate_or_invented_evidence_is_rejected(self):
        changes = [
            lambda f: f["reviews"][0]["taskPassed"]["evidence"][0].update(path=self.ids[1] + "/final.md"),
            lambda f: f["reviews"][0]["taskPassed"]["evidence"][0].update(quote="Invented evidence"),
            lambda f: f["reviews"][0]["verificationScore"].update(value=True),
        ]
        for change in changes:
            altered = copy.deepcopy(self.form); change(altered)
            with self.assertRaises(ValueError):self.check(altered)

    def test_unknown_and_negative_judgments_go_to_human_queue_plus_sample(self):
        self.form["reviews"][1]["taskPassed"]["value"] = None
        self.assertEqual(1, self.check()["unresolvedJudgments"])
        queue = review.queue(self.form)
        self.assertTrue(all(c["suggestedHumanReview"] for c in queue["candidates"]))
        self.assertFalse(queue["blocksDevelopmentCli"])
        self.form["reviews"][1]["taskPassed"]["value"] = False
        self.assertIn("model-reported-failure-or-gap", review.queue(self.form)["candidates"][1]["reasons"])

    def test_model_cannot_override_failing_original_oracle(self):
        path = self.packet / self.ids[0] / "evidence.json"
        evidence = review.common.read_json(path)
        evidence["externalGrading"]["suites"][1]["totals"]["failures"] = 1
        review.common.dump(path, evidence); self.reseal()
        with self.assertRaisesRegex(ValueError, "contradicts required objective"):
            self.check()

    def test_model_cannot_treat_inconclusive_execution_as_pass_even_with_zero_test_failures(self):
        path = self.packet / self.ids[0] / "evidence.json"
        evidence = review.common.read_json(path)
        evidence["externalGrading"]["gradeStatus"] = "inconclusive-grading-execution-or-evidence-error"
        review.common.dump(path, evidence); self.reseal()
        with self.assertRaisesRegex(ValueError, "contradicts required objective"):
            self.check()

    def test_modified_or_extra_packet_files_are_rejected(self):
        path = self.packet / self.ids[0] / "final.md"
        original = path.read_text(); path.write_text("tampered")
        with self.assertRaisesRegex(ValueError, "evidence changed"):self.check()
        path.write_text(original); (self.packet / "extra.md").write_text("unbound evidence")
        with self.assertRaisesRegex(ValueError, "file set changed"):self.check()

    def test_human_sample_contains_only_selected_case_and_no_model_scores(self):
        self.check()
        model_manifest = review.common.read_json(self.packet / "manifest.json")
        registered = self.root / "source-registration"
        source = registered / "reviewer"; source.mkdir(parents=True)
        files = {}
        for name in model_manifest["files"]:
            target = source / name; target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes((self.packet / name).read_bytes())
            files[name] = review.common.digest(target.read_bytes())
        (source / "README.md").write_text("Synthetic human instructions.\n")
        files["README.md"] = review.common.digest((source / "README.md").read_bytes())
        review.common.dump(source / "manifest.json", {"packetId": "original", "candidates": self.ids, "files": files})
        source_hash = review.common.digest((source / "manifest.json").read_bytes())
        review.common.dump(registered / "evaluator-map.json", {"packetId": "original", "manifestSha256": source_hash})
        mapping = review.common.read_json(self.map)
        mapping.update(sourceRegistrationManifestSha256=source_hash,
                       mapping={self.ids[0]: {"condition": "baseline"}, self.ids[1]: {"condition": "treatment"}})
        review.common.dump(self.map, mapping)
        out = self.root / "human-sample"
        result = review.prepare_human_sample(self.packet, self.root / "model-scores.json", self.map, out)
        self.assertEqual(1, result["candidates"])
        form = review.common.read_json(out / "reviewer/scores.json")
        self.assertEqual([self.ids[0]], [r["candidateId"] for r in form["reviews"]])
        self.assertTrue(all(form["reviews"][0][key]["value"] is None for key in review.common.METRICS))
        self.assertFalse(form["reviewer"]["humanAttestation"])
        self.assertFalse((out / "reviewer" / self.ids[1]).exists())
        self.assertFalse((out / "reviewer/model-scores.json").exists())
        with self.assertRaisesRegex(ValueError, "attestation"):
            review.common.validate(out / "reviewer", out / "reviewer/scores.json", out / "evaluator-map.json")
        with self.assertRaisesRegex(ValueError, "already exists"):
            review.prepare_human_sample(self.packet, self.root / "model-scores.json", self.map, out)


if __name__ == "__main__":unittest.main()
