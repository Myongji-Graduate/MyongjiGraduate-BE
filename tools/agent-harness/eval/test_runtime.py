"""Regression tests for shell environment evidence; no model/server/process calls."""
import importlib.util
from pathlib import Path
import unittest

spec = importlib.util.spec_from_file_location('runtime_check', Path(__file__).with_name('check-runtime.py'))
runtime = importlib.util.module_from_spec(spec)
spec.loader.exec_module(runtime)


class EnvironmentEvidenceTest(unittest.TestCase):
    def setUp(self):
        self.expected = {key: f'pinned-{key}' for key in runtime.PROBE_ENV_KEYS}

    def test_all_pinned_values_are_present_and_unchanged(self):
        runtime.validate_environment(self.expected, dict(self.expected))

    def test_json_omitted_variables_are_rejected_even_if_java_can_use_path(self):
        for key in runtime.PROBE_ENV_KEYS:
            with self.subTest(key=key):
                observed = dict(self.expected)
                del observed[key]
                with self.assertRaisesRegex(RuntimeError, 'omitted'):
                    runtime.validate_environment(self.expected, observed)

    def test_login_shell_path_reordering_is_rejected(self):
        observed = dict(self.expected, PATH='/System/Cryptexes/App/usr/bin:' + self.expected['PATH'])
        with self.assertRaisesRegex(RuntimeError, 'PATH'):
            runtime.validate_environment(self.expected, observed)


if __name__ == '__main__':
    unittest.main()
