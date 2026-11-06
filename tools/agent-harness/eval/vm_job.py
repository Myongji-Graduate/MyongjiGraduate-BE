"""Own one local sbx client and its VM cleanup in an independent watchdog.

All harness writers must reserve the same VM name with lease() until cleanup
finishes. The native CLI uses names; external CLI/UI replacement during a job
is outside that cooperative ownership contract.
"""
import argparse
from contextlib import contextmanager, nullcontext
import fcntl
import json
import os
from pathlib import Path
import re
import signal
import stat
import subprocess
import sys
import time


def save(path, value):
    temporary = path.with_suffix('.new')
    temporary.write_text(json.dumps(value, indent=2) + '\n')
    temporary.replace(path)


@contextmanager
def lease(vm_name):
    if not re.fullmatch(r'dev122-[a-zA-Z0-9.-]+', vm_name):
        raise ValueError('Invalid DEV-122 VM name')
    # Stable across callers with different TMPDIR values, on macOS and Linux.
    directory = Path('/tmp') / ('dev122-vm-leases-' + str(os.getuid()))
    directory.mkdir(mode=0o700, exist_ok=True)
    if directory.is_symlink() or directory.stat().st_uid != os.getuid():
        raise RuntimeError('VM lease directory is not owned by this user')
    path = directory / (vm_name + '.lock')
    descriptor = os.open(path, os.O_CREAT | os.O_RDWR | os.O_NOFOLLOW, 0o600)
    try:
        fcntl.flock(descriptor, fcntl.LOCK_EX | fcntl.LOCK_NB)
        # Inherit the same open file description: owner death does not release
        # this reservation while the watchdog still owns its descriptor.
        yield descriptor
    finally:
        os.close(descriptor)


def signal_group(group, sig):
    try:
        os.killpg(group, sig)
    except ProcessLookupError:
        pass
    except PermissionError:
        # macOS can return EPERM when only zombies remain in the group.
        rows = subprocess.check_output(['ps', '-axo', 'pgid=,stat='], text=True, timeout=5)
        if any(int(parts[0]) == group and not parts[1].startswith('Z')
               for line in rows.splitlines() if len(parts := line.split()) == 2):
            raise


def watchdog(state_path):
    state = json.loads(state_path.read_text())
    if not stat.S_ISREG(os.fstat(state['leaseFd']).st_mode):
        raise RuntimeError('Missing inherited VM lease')
    signal.signal(signal.SIGCHLD, signal.SIG_DFL)
    env = {**os.environ, 'SSH_AUTH_SOCK': ''}
    commands, errors = [], []
    process = None
    exit_code = None
    reason = 'supervisor-error'
    started = time.monotonic()
    identity_checked = False

    def run(args):
        result = subprocess.run([state['sbx'], *args], capture_output=True,
                                text=True, env=env, timeout=30)
        commands.append({'args': args, 'exitCode': result.returncode})
        if result.returncode:
            raise RuntimeError('VM control command failed')
        return result.stdout

    def verify_identity():
        matches = [row for row in json.loads(run(['ls', '--json']))
                   if row['name'] == state['vmName']]
        if len(matches) != 1 or matches[0]['id'] != state['vmId']:
            raise RuntimeError('VM identity changed; refusing unrelated cleanup')

    def stop_reason():
        current = json.loads(state_path.read_text())
        if os.getppid() != current['ownerPid']:
            return 'owner-exited'
        if current.get('done'):
            return 'interrupted'
        if time.monotonic() >= current['deadline']:
            return 'deadline'
        return None

    try:
        verify_identity()
        identity_checked = True
        reason = stop_reason()
        if reason is None:
            # The watchdog owns launch as well as cleanup. There is no parent
            # launch/PID-publication window, and no late unowned exec client.
            with (state_path.parent / 'prompt.txt').open() as prompt, \
                    (state_path.parent / 'events.jsonl').open('w') as output, \
                    (state_path.parent / 'stderr.log').open('w') as error:
                process = subprocess.Popen(
                    [state['sbx'], 'exec', state['vmName'], *state['guestCommand']],
                    env=os.environ.copy(), stdin=prompt, stdout=output, stderr=error,
                    start_new_session=True)
                save(state_path.parent / 'watchdog-runtime.json',
                     {'clientPid': process.pid, 'watchdogPid': os.getpid()})
                while True:
                    reason = stop_reason()
                    if reason:
                        break
                    # Keep an exited leader unreaped until its group is killed.
                    # Its reserved PID prevents process-group identifier reuse.
                    exited = os.waitid(os.P_PID, process.pid,
                                       os.WEXITED | os.WNOHANG | os.WNOWAIT)
                    if exited:
                        exit_code = (exited.si_status if exited.si_code == os.CLD_EXITED
                                     else -exited.si_status)
                        reason = 'job-finished'
                        break
                    time.sleep(0.1)
    except Exception as error:
        errors.append(type(error).__name__ + ': ' + str(error))
        reason = 'supervisor-error'
    finally:
        if process is not None:
            try:
                # Cancel a delayed host client before stopping the VM, so that
                # it cannot restart the VM after cleanup.
                signal_group(process.pid, signal.SIGTERM)
                time.sleep(0.15)
                signal_group(process.pid, signal.SIGKILL)
                process.wait(timeout=5)
                if exit_code is None:
                    exit_code = process.returncode
            except Exception as error:
                errors.append(type(error).__name__ + ': ' + str(error))
        if identity_checked:
            try:
                verify_identity()
                try:
                    run(['stop', state['vmName']])
                except Exception as error:
                    errors.append(type(error).__name__ + ': ' + str(error))
                for resource in ['**', '0.0.0.0/0', '::/0']:
                    try:
                        run(['policy', 'deny', 'network', '--sandbox',
                             state['vmName'], resource])
                    except Exception as error:
                        errors.append(type(error).__name__ + ': ' + str(error))
                current = json.loads(run(['ls', '--json']))
                if not any(row['id'] == state['vmId'] and row['status'] == 'stopped'
                           for row in current):
                    raise RuntimeError('VM stop not confirmed')
            except Exception as error:
                errors.append(type(error).__name__ + ': ' + str(error))
        cleanup = {'reason': reason, 'commands': commands, 'errors': errors,
                   'vmStopped': identity_checked and not errors,
                   'networkDenyRestored': identity_checked and not errors}
        save(state_path.parent / 'watchdog-result.json', cleanup)
        status = ('completed' if exit_code == 0 else 'execution-error') \
            if reason == 'job-finished' else {
                'deadline': 'timeout', 'owner-exited': 'owner-exited',
                'interrupted': 'interrupted',
            }.get(reason, 'supervisor-error')
        if errors:
            status = 'cleanup-error'
        save(state_path.parent / 'result.json',
             {'status': status, 'exitCode': exit_code,
              'elapsedSeconds': round(time.monotonic() - started, 3),
              'cleanup': cleanup})
    return int(bool(errors))


def supervise(sbx, vm_name, vm_id, guest_command, prompt, environment, output, budget,
              *, lease_descriptor=None):
    if not 0 < budget <= 1200 or not hasattr(os, 'WNOWAIT'):
        raise ValueError('Bounded jobs require POSIX WNOWAIT and a <=1200s budget')
    if len(prompt.encode()) > 131072:
        raise ValueError('Prompt exceeds frozen job limit')
    output.mkdir(exist_ok=False)
    (output / 'prompt.txt').write_text(prompt)
    state_path = output / 'watchdog-state.json'
    previous = {}

    def interrupted(signum, frame):
        raise InterruptedError('VM job interrupted')

    reservation = lease(vm_name) if lease_descriptor is None else nullcontext(lease_descriptor)
    with reservation as descriptor:
        state = {'sbx': str(sbx), 'vmName': vm_name, 'vmId': vm_id,
                 'ownerPid': os.getpid(), 'deadline': time.monotonic() + budget,
                 'guestCommand': guest_command, 'leaseFd': descriptor, 'done': False}
        save(state_path, state)
        watcher = subprocess.Popen([sys.executable, '-B', __file__, '--watch', str(state_path)],
                                   env=environment, start_new_session=True,
                                   pass_fds=(descriptor,))
        for sig in [signal.SIGINT, signal.SIGTERM]:
            previous[sig] = signal.signal(sig, interrupted)
        try:
            watcher.wait(timeout=budget + 240)
        except (InterruptedError, KeyboardInterrupt):
            state['done'] = True
            save(state_path, state)
            watcher.wait(timeout=240)
        finally:
            for sig, handler in previous.items():
                signal.signal(sig, handler)
        result_path = output / 'result.json'
        if watcher.returncode != 0 or not result_path.exists():
            raise RuntimeError('Independent VM cleanup failed; do not continue the pair')
        return json.loads(result_path.read_text())


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--watch', type=Path, required=True)
    args = parser.parse_args()
    raise SystemExit(watchdog(args.watch))
