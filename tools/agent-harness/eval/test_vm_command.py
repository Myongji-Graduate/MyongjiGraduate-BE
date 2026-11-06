import copy
import importlib.util
from pathlib import Path
import unittest
from unittest import mock

spec = importlib.util.spec_from_file_location('vm_command', Path(__file__).with_name('check-vm-command.py'))
vm = importlib.util.module_from_spec(spec)
spec.loader.exec_module(vm)


class VmCommandTest(unittest.TestCase):
    def test_skill_cleanup_failure_still_closes_server_and_propagates(self):
        server = mock.Mock()
        with mock.patch.object(vm.shutil, 'rmtree', side_effect=OSError('synthetic removal failure')):
            with self.assertRaisesRegex(OSError, 'synthetic removal failure'):
                vm.cleanup_server(server, Path('/synthetic/skill-directory'))
        server.close.assert_called_once()

    def test_server_cleanup_failure_propagates(self):
        server = mock.Mock()
        server.close.side_effect = RuntimeError('synthetic close failure')
        with self.assertRaisesRegex(RuntimeError, 'synthetic close failure'):
            vm.cleanup_server(server, None)

    def test_child_environment_excludes_inherited_credentials_and_config(self):
        inherited = {'HOME': '/home/agent', 'USER': 'agent', 'OPENAI_API_KEY': 'synthetic-openai',
                     'GH_TOKEN': 'synthetic-github', 'AWS_SECRET_ACCESS_KEY': 'synthetic-aws',
                     'CODEX_HOME': '/unrelated', 'GIT_CONFIG_GLOBAL': '/private/config', 'PATH': '/wrong'}
        pinned = {'PATH': '/usr/bin', 'GIT_CONFIG_GLOBAL': '/dev/null'}
        actual = vm.process_environment(pinned, inherited, Path('/opt/dev122/codex-state'))
        self.assertEqual(actual, {'HOME': '/home/agent', 'USER': 'agent', **pinned,
                                  'CODEX_HOME': '/opt/dev122/codex-state'})

    def test_model_start_methods_rejected_before_any_server_io(self):
        server = vm.Server.__new__(vm.Server)
        for method in ['thread/start', 'thread/resume', 'turn/start', 'models/list', 'account/login/start']:
            with self.subTest(method=method), self.assertRaisesRegex(RuntimeError, 'cannot start'):
                server.call(method, {})

    def test_effective_profile_detects_model_tools_and_mcp_drift(self):
        expected = {'model': 'gpt-6-astra', 'model_reasoning_effort': 'high',
                    'allow_login_shell': False, 'web_search': 'disabled', 'features': {'plugins': False}}
        actual = {**copy.deepcopy(expected), 'mcp_servers': {}}
        vm.validate_effective_config(expected, actual)
        for key, value in [('model', 'wrong'), ('model_reasoning_effort', 'low'),
                           ('allow_login_shell', True), ('web_search', 'live'),
                           ('features', {'plugins': True}), ('mcp_servers', {'unexpected': {}})]:
            changed = copy.deepcopy(actual)
            changed[key] = value
            with self.subTest(key=key), self.assertRaises(RuntimeError):
                vm.validate_effective_config(expected, changed)

    def test_shell_omissions_extras_and_credentials_are_rejected(self):
        expected = {'PATH': '/usr/bin', 'LANG': 'C.utf8'}
        vm.validate_shell_environment(expected, {'environment': expected, 'credentialNames': []})
        for observed in [
            {'environment': {'LANG': 'C.utf8'}, 'credentialNames': []},
            {'environment': {**expected, 'EXTRA': 'value'}, 'credentialNames': []},
            {'environment': expected, 'credentialNames': ['GH_TOKEN']},
            {'environment': expected},
        ]:
            with self.subTest(observed=observed), self.assertRaises(RuntimeError):
                vm.validate_shell_environment(expected, observed)


if __name__ == '__main__':
    unittest.main()
