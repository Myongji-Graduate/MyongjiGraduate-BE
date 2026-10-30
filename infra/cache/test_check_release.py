import tempfile
from pathlib import Path
import unittest

from check_release import REQUIRED_MIGRATIONS, check_release


class ReleaseCheckTest(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self.tmp.cleanup)
        self.directory = Path(self.tmp.name)
        for filename in REQUIRED_MIGRATIONS.values():
            (self.directory / filename).write_text("-- synthetic migration\n")

    def test_accepts_cache_first_release_without_versions_12_and_13_in_all_modes(self):
        for mode in ("off", "shadow", "on"):
            with self.subTest(mode=mode):
                self.assertEqual([], check_release(self.directory, mode, "prod-dev121-v3"))

    def test_rejects_later_work_using_old_versions_even_in_off(self):
        for filename in ("V12__later_work.sql", "V13__later_work.sql", "V012_1__later_work.sql"):
            with self.subTest(filename=filename):
                path = self.directory / filename
                path.write_text("-- synthetic late migration\n")
                self.assertTrue(any("above the latest deployed version" in e
                                    for e in check_release(self.directory, "off", "prod")))
                path.unlink()

    def test_rejects_missing_history_or_cache_migration(self):
        for version in ("1", "11", "14", "15"):
            with self.subTest(version=version):
                path = self.directory / REQUIRED_MIGRATIONS[version]
                content = path.read_text()
                path.unlink()
                self.assertTrue(any(path.name in e for e in check_release(self.directory, "off", "prod")))
                path.write_text(content)

    def test_accepts_future_work_above_cache_versions(self):
        for version in (16, 17):
            (self.directory / f"V{version}__synthetic_future_work.sql").write_text("-- future\n")
        self.assertEqual([], check_release(self.directory, "off", "prod"))

    def test_rejects_wrong_file_at_required_version(self):
        (self.directory / REQUIRED_MIGRATIONS["14"]).rename(self.directory / "V14__other.sql")
        self.assertTrue(any(REQUIRED_MIGRATIONS["14"] in e for e in check_release(self.directory, "off", "prod")))

    def test_rejects_equivalent_duplicate_versions(self):
        for filename in ("V014__duplicate.sql", "V14_0__duplicate.sql", "V14.0.0__duplicate.sql"):
            with self.subTest(filename=filename):
                path = self.directory / filename
                path.write_text("-- duplicate\n")
                self.assertTrue(any("Duplicate" in e for e in check_release(self.directory, "off", "prod")))
                path.unlink()

    def test_rejects_invalid_settings(self):
        for mode, namespace in (("enabled", "prod"), ("on", ""), ("on", "prod\n"), ("on", "prod:1")):
            with self.subTest(mode=mode, namespace=namespace):
                self.assertTrue(check_release(self.directory, mode, namespace))

    def test_rejects_missing_directory_and_bad_filename(self):
        self.assertTrue(check_release(self.directory / "missing", "off", "prod"))
        (self.directory / "V16_broken.sql").write_text("-- invalid\n")
        self.assertTrue(any("Invalid" in e for e in check_release(self.directory, "off", "prod")))


if __name__ == "__main__":
    unittest.main()
