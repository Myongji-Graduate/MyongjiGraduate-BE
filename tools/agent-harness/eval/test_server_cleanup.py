"""Owned process-group cleanup checks; all process control is mocked."""
import importlib.util
from pathlib import Path
import signal
from types import SimpleNamespace
import unittest
from unittest import mock


MODULES = []
for name in ['check-runtime.py', 'check-vm-command.py']:
    spec = importlib.util.spec_from_file_location(name, Path(__file__).with_name(name))
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    MODULES.append(module)


class ServerCleanupTest(unittest.TestCase):
    def server(self, module):
        server = module.Server.__new__(module.Server)
        server.process = SimpleNamespace(pid=12345, stdin=mock.Mock(), stdout=mock.Mock(),
                                         wait=mock.Mock(), returncode=None)
        server.reader = mock.Mock()
        server.stderr = mock.Mock()
        return server

    def assert_streams_closed(self, server):
        server.reader.join.assert_called_once_with(timeout=1)
        server.process.stdout.close.assert_called_once()
        server.stderr.close.assert_called_once()

    def test_group_signals_never_target_a_reaped_leaders_reusable_id(self):
        for module in MODULES:
            with self.subTest(module=module.__name__):
                server = self.server(module)
                def reap(**kwargs):
                    server.process.returncode = 0
                def signal_owned(group, sig):
                    self.assertIsNone(server.process.returncode, 'Leader was already reaped')
                    self.assertEqual(server.process.pid, group)
                server.process.wait.side_effect = reap
                with mock.patch.object(module.os, 'killpg', side_effect=signal_owned) as kill, \
                        mock.patch.object(module.time, 'sleep'):
                    server.close()
                self.assertEqual([mock.call(12345, signal.SIGTERM), mock.call(12345, signal.SIGKILL)],
                                 kill.call_args_list)
                server.process.wait.assert_called_once_with(timeout=5)
                self.assert_streams_closed(server)

    def test_broken_stdin_still_terminates_group_and_closes_streams(self):
        for module in MODULES:
            with self.subTest(module=module.__name__):
                server = self.server(module)
                server.process.stdin.close.side_effect = BrokenPipeError('synthetic closed peer')
                with mock.patch.object(module.os, 'killpg') as kill, mock.patch.object(module.time, 'sleep'):
                    server.close()
                self.assertEqual(2, kill.call_count)
                server.process.wait.assert_called_once_with(timeout=5)
                self.assert_streams_closed(server)

    def test_other_stdin_errors_propagate_after_owned_cleanup(self):
        for module in MODULES:
            with self.subTest(module=module.__name__):
                server = self.server(module)
                server.process.stdin.close.side_effect = OSError('synthetic stdin error')
                with mock.patch.object(module.os, 'killpg') as kill, mock.patch.object(module.time, 'sleep'):
                    with self.assertRaisesRegex(OSError, 'synthetic stdin error'):
                        server.close()
                self.assertEqual(2, kill.call_count)
                server.process.wait.assert_called_once_with(timeout=5)
                self.assert_streams_closed(server)

    def test_permission_error_for_only_zombies_still_reaps_and_closes_streams(self):
        for module in MODULES:
            with self.subTest(module=module.__name__):
                server = self.server(module)
                with mock.patch.object(module.os, 'killpg', side_effect=PermissionError), \
                        mock.patch.object(module.subprocess, 'check_output', return_value='12345 Z\n7 S\n'):
                    server.close()
                server.process.wait.assert_called_once_with(timeout=5)
                self.assert_streams_closed(server)

    def test_live_group_permission_errors_are_reported_and_streams_are_closed(self):
        for module in MODULES:
            with self.subTest(module=module.__name__):
                server = self.server(module)
                with mock.patch.object(module.os, 'killpg', side_effect=PermissionError), \
                        mock.patch.object(module.subprocess, 'check_output', return_value='12345 S\n'):
                    with self.assertRaises(PermissionError):server.close()
                self.assert_streams_closed(server)


if __name__ == '__main__':unittest.main()
