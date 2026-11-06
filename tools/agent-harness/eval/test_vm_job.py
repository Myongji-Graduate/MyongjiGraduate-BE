import json
import os
from pathlib import Path
import subprocess
import tempfile
import sys
import time
import unittest
from unittest import mock

import vm_job


class VmJobTest(unittest.TestCase):
    def run_watchdog(self, directory, *, current_id='owned-id', fail_policy=False):
        state_path = Path(directory) / 'state.json'
        calls = []

        def execute(argv, **kwargs):
            calls.append(argv)
            if argv[1:] == ['ls', '--json']:
                return subprocess.CompletedProcess(argv, 0, json.dumps([
                    {'name': 'dev122-synthetic', 'id': current_id, 'status': 'stopped'}]), '')
            failed = fail_policy and 'policy' in argv
            return subprocess.CompletedProcess(argv, int(failed), '', '')

        with vm_job.lease('dev122-synthetic') as descriptor:
            vm_job.save(state_path, {'sbx': '/synthetic/sbx', 'vmName': 'dev122-synthetic',
                                    'vmId': 'owned-id', 'ownerPid': 123,
                                    'leaseFd': descriptor,
                                    'deadline': time.monotonic() + 1, 'done': True})
            with mock.patch.object(vm_job.os, 'getppid', return_value=123), \
                    mock.patch.object(vm_job.signal, 'signal'), \
                    mock.patch.object(vm_job.subprocess, 'run', side_effect=execute):
                code = vm_job.watchdog(state_path)
        return code, calls, json.loads((Path(directory) / 'watchdog-result.json').read_text())

    def test_cancellation_confirms_vm_stop_before_finish(self):
        with tempfile.TemporaryDirectory() as directory:
            code, calls, result = self.run_watchdog(directory)
            self.assertEqual(code, 0)
            self.assertEqual(result['reason'], 'interrupted')
            self.assertTrue(result['vmStopped'])
            self.assertIn(['/synthetic/sbx', 'stop', 'dev122-synthetic'], calls)

    def test_identity_change_never_stops_an_unrelated_vm(self):
        with tempfile.TemporaryDirectory() as directory:
            code, calls, result = self.run_watchdog(directory, current_id='unrelated-id')
            self.assertEqual(code, 1)
            self.assertFalse(result['vmStopped'])
            self.assertEqual(len(calls), 1)

    def test_failed_policy_still_attempts_stop_but_never_reports_success(self):
        with tempfile.TemporaryDirectory() as directory:
            code, calls, result = self.run_watchdog(directory, fail_policy=True)
            self.assertEqual(code, 1)
            self.assertFalse(result['networkDenyRestored'])
            self.assertIn(['/synthetic/sbx', 'stop', 'dev122-synthetic'], calls)

    def test_second_harness_writer_cannot_acquire_an_active_vm_name(self):
        with vm_job.lease('dev122-lease-synthetic'):
            with self.assertRaises(BlockingIOError):
                with vm_job.lease('dev122-lease-synthetic'):
                    self.fail('A second writer acquired the active VM')

    def test_different_tmpdirs_share_the_same_user_vm_lease(self):
        with tempfile.TemporaryDirectory() as directory:
            first_tmp = Path(directory) / 'first'
            second_tmp = Path(directory) / 'second'
            first_tmp.mkdir()
            second_tmp.mkdir()
            code = ("import sys;sys.path.insert(0,sys.argv[1]);from vm_job import lease;"
                    "ctx=lease('dev122-cross-process-synthetic');ctx.__enter__();"
                    "print('locked',flush=True);sys.stdin.read();ctx.__exit__(None,None,None)")
            module = str(Path(vm_job.__file__).parent)
            owner = subprocess.Popen([sys.executable, '-B', '-c', code, module],
                                     env={**os.environ, 'TMPDIR': str(first_tmp)},
                                     stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                                     stderr=subprocess.PIPE, text=True)
            try:
                self.assertEqual(owner.stdout.readline().strip(), 'locked')
                contender = subprocess.run([sys.executable, '-B', '-c', code, module],
                                           env={**os.environ, 'TMPDIR': str(second_tmp)},
                                           input='', capture_output=True, text=True, timeout=5)
                self.assertNotEqual(contender.returncode, 0)
                self.assertIn('BlockingIOError', contender.stderr)
            finally:
                owner.stdin.close()
                owner.wait(timeout=5)
                owner.stdout.close()
                owner.stderr.close()


if __name__ == '__main__':
    unittest.main()
