import importlib.util
import json
from pathlib import Path
import tempfile
import unittest

spec = importlib.util.spec_from_file_location("t04_review", Path(__file__).with_name("human-review.py"))
review = importlib.util.module_from_spec(spec)
spec.loader.exec_module(review)


class T04ReviewTest(unittest.TestCase):
    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.source = self.root / "evidence"
        self.source.mkdir()
        self.out = self.root / "registered"
        mapping, records = {}, []
        for index, condition in enumerate(["without-skill", "with-skill"], 1):
            alias = "candidate-" + str(index) * 8
            mapping[alias] = condition
            folder = self.source / "human-review" / alias
            folder.mkdir(parents=True)
            product, diff = "// synthetic identical product\n", "+// synthetic identical product\n"
            (folder / "product.java").write_text(product)
            (folder / "product.diff").write_text(diff)
            (folder / "final.md").write_text("Unit and synthetic DB verified. HTTP/UI untested.\n")
            review.dump(folder / "evidence.json", {"originalOracle": "passed"})
            added = folder / f"added-tests/src/test/java/New{index}Test.java"
            added.parent.mkdir(parents=True)
            added.write_text(f"// synthetic new test {index}\n")
            records.append({"condition": condition, "candidateSourceSha256": review.digest(product.encode()),
                            "finalProductDiffSha256": review.digest(diff.encode())})
        (self.source / "human-review/README.md").write_text("Synthetic ungraded fixture.\n")
        review.dump(self.source / "human-review/scores.json", {"allValues": None})
        review.dump(self.source / "evaluator-map.json", mapping)
        self.result = self.source / "result.json"
        review.dump(self.result, {"taskId": "T04", "pilotId": "T04-v1", "agentExecutionsStarted": 2,
                                 "records": records})
        self.preserve()
        self.expected_anchor = review.digest((self.source / "preservation.json").read_bytes())

    def preserve(self):
        review.dump(self.source / "preservation.json", {"files": [
            {"path": str(p.relative_to(self.source)), "sha256": review.digest(p.read_bytes())}
            for p in self.source.rglob("*") if p.is_file() and p.name != "preservation.json"]})

    def prepare(self):
        return review.prepare_t04(self.source, self.result, self.out, self.expected_anchor)

    def test_existing_t04_packet_registers_unscored_and_uses_same_validator(self):
        self.assertEqual("packet-prepared-ungraded", self.prepare()["status"])
        packet = self.out / "reviewer"
        form = review.read_json(packet / "scores.json")
        self.assertTrue(all(metric["value"] is None for row in form["reviews"] for key, metric in row.items()
                            if key in review.METRICS))
        with self.assertRaisesRegex(ValueError, "attestation"):
            review.validate(packet, packet / "scores.json", self.out / "evaluator-map.json")
        form["reviewer"] = {"name": "Synthetic test, not a person", "completedAt": "2026-11-03T12:00:00+09:00",
                            "priorExposure": "Synthetic fixture", "humanAttestation": True}
        for row in form["reviews"]:
            for key in review.METRICS:
                row[key] = {"value": 2 if key == "verificationScore" else False,
                            "reason": "Synthetic fixture only", "evidence": [{
                                "path": row["candidateId"] + "/final.md",
                                "quote": "HTTP/UI untested."}]}
        complete = self.root / "synthetic-scores.json"
        review.dump(complete, form)
        self.assertEqual("human-form-valid", review.validate(packet, complete, self.out / "evaluator-map.json")["status"])
        with self.assertRaises(FileExistsError):
            self.prepare()

    def test_changed_or_unrecorded_source_evidence_is_rejected_before_output(self):
        path = self.source / "human-review/candidate-11111111/final.md"
        path.write_text("invented passing result")
        with self.assertRaisesRegex(ValueError, "source review evidence changed"):
            self.prepare()
        self.assertFalse(self.out.exists())
        path.write_text("Unit and synthetic DB verified. HTTP/UI untested.\n")
        (path.parent / "unrecorded.md").write_text("unrecorded")
        with self.assertRaisesRegex(ValueError, "source review evidence changed"):
            self.prepare()

    def test_swapped_map_with_identical_products_is_rejected_if_local_manifest_is_renewed(self):
        path = self.source / "evaluator-map.json"
        mapping = review.read_json(path)
        values = list(mapping.values())[::-1]
        review.dump(path, dict(zip(mapping, values)))
        self.preserve()
        records = review.read_json(self.result)["records"]
        self.assertEqual(1, len({r["candidateSourceSha256"] for r in records}))
        with self.assertRaisesRegex(ValueError, "source preservation fingerprint mismatch"):
            self.prepare()
        self.assertFalse(self.out.exists())

    def test_deleted_preserved_added_test_is_rejected_before_output(self):
        (self.source / "human-review/candidate-11111111/added-tests/src/test/java/New1Test.java").unlink()
        with self.assertRaisesRegex(ValueError, "file set changed"):
            self.prepare()
        self.assertFalse(self.out.exists())

    def test_source_packet_symlink_is_rejected(self):
        path = self.source / "human-review/candidate-11111111/final.md"
        outside = self.root / "outside.md"
        outside.write_bytes(path.read_bytes())
        path.unlink()
        path.symlink_to(outside)
        with self.assertRaisesRegex(ValueError, "symlink"):
            self.prepare()
        self.assertFalse(self.out.exists())


if __name__ == "__main__":
    unittest.main()
