#!/usr/bin/env python3
"""Model-free macOS preflight for an already prepared DEV-122 unit evaluation copy."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import queue
import shlex
import signal
import subprocess
import sys
import threading
import time
import tomllib
import xml.etree.ElementTree as ET

PROBE_ENV_KEYS = ('PATH', 'TMPDIR', 'GRADLE_USER_HOME', 'JAVA_HOME', 'JAVA_TOOL_OPTIONS',
                  'GIT_CONFIG_GLOBAL', 'GIT_CONFIG_NOSYSTEM')


def require(condition, message):
    if not condition:
        raise RuntimeError(message)


def save(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')


def validate_environment(expected, observed):
    require(set(observed) == set(expected), 'Shell omitted or added pinned environment keys')
    for key, value in expected.items():
        require(observed[key] == value, f'Shell changed pinned {key}')


class Server:
    def __init__(self, codex, workspace, env, output):
        self.output = output
        self.workspace = workspace
        self.messages = queue.Queue()
        self.sequence = 0
        self.stderr = (output / 'app-server.stderr.log').open('w')
        self.process = subprocess.Popen(
            [str(codex), 'app-server', '--strict-config'], cwd=workspace, env=env,
            stdin=subprocess.PIPE, stdout=subprocess.PIPE, stderr=self.stderr,
            text=True, bufsize=1, start_new_session=True,
        )
        self.reader = threading.Thread(target=self.read, daemon=True)
        self.reader.start()

    def read(self):
        try:
            for line in self.process.stdout:
                self.messages.put(json.loads(line))
        except Exception as error:
            self.messages.put(error)
        finally:
            self.messages.put(EOFError('app-server closed its output'))

    def send(self, message):
        self.process.stdin.write(json.dumps(message) + '\n')
        self.process.stdin.flush()

    def call(self, method, params, timeout=30):
        self.sequence += 1
        self.send(dict(id=self.sequence, method=method, params=params))
        deadline = time.monotonic() + timeout
        while True:
            message = self.messages.get(timeout=max(0, deadline - time.monotonic()))
            if isinstance(message, Exception):
                raise message
            if message.get('id') == self.sequence:
                require('error' not in message, f'{method}: {message.get("error")}')
                return message['result']
            # No thread or turn is started; unexpected requests are errors.
            require('id' not in message, f'Unexpected server request: {message.get("method")}')

    def command(self, label, command, timeout=20000):
        result = self.call('command/exec', dict(
            command=['/bin/zsh', '-c', command], cwd=str(self.workspace),
            timeoutMs=timeout, outputBytesCap=300000,
        ), timeout=timeout / 1000 + 20)
        save(self.output / f'{label}.json', dict(command=command, **result))
        print(f'{label}: exit {result["exitCode"]}', flush=True)
        return result

    def close(self):
        try:
            try:
                self.process.stdin.close()
            except BrokenPipeError:
                pass
        finally:
            try:
                # Retain the unreaped leader until the final group signal:
                # its PID must not be reused by an unrelated process group.
                for sig in (signal.SIGTERM, signal.SIGKILL):
                    try:
                        os.killpg(self.process.pid, sig)
                    except ProcessLookupError:
                        break
                    except PermissionError:
                        # macOS may reject a group containing only zombies.
                        rows = subprocess.check_output(['ps', '-axo', 'pgid=,stat='], text=True, timeout=5)
                        if any(int(parts[0]) == self.process.pid and not parts[1].startswith('Z')
                               for line in rows.splitlines() if len(parts := line.split()) == 2):
                            raise
                        break
                    time.sleep(0.2)
                self.process.wait(timeout=5)
            finally:
                try:
                    self.reader.join(timeout=1)
                finally:
                    try:
                        self.process.stdout.close()
                    finally:
                        self.stderr.close()


def check(args, output):
    require(sys.platform == 'darwin', 'This frozen profile is macOS-only')
    case = args.case_root.resolve(strict=True)
    require(case.is_relative_to('/private/tmp'), 'Use a prepared case under /private/tmp')
    workspace = case / 'workspace'
    require((workspace / '.git').is_dir(), 'Expected an independent Git copy, not a linked worktree')
    for name in ('workspace', 'codex-home', 'gradle-home', 'tmp'):
        path = case / name
        require(path.is_dir() and path.resolve() == path, f'Expected real case directory: {name}')
    require(not (case / 'codex-home/auth.json').exists() and not (case / 'codex-home/auth.json').is_symlink(),
            'Remove the evaluation authentication link before this model-free check')
    template = Path(__file__).with_name('codex-unit-config.toml.template').read_text()
    config_text = (case / 'codex-home/config.toml').read_text()
    require(config_text == template.replace('${CASE_ROOT}', str(case)), 'Case config differs from frozen runtime template')
    config = tomllib.loads(config_text)
    env = dict(config['shell_environment_policy']['set'])
    env.update(CODEX_HOME=str(case / 'codex-home'), HOME=os.environ['HOME'], LANG='en_US.UTF-8', TERM='dumb')
    codex = args.codex.resolve(strict=True)
    version = subprocess.check_output([str(codex), '--version'], env=env, text=True, timeout=10).strip()
    require(version == 'codex-cli 0.166.0', f'Unexpected Codex version: {version}')
    server = Server(codex, workspace, env, output)
    try:
        server.call('initialize', dict(clientInfo=dict(name='dev122-runtime-check', version='1'),
                                      capabilities=dict(experimentalApi=True)))
        server.send(dict(method='initialized', params={}))
        effective = server.call('config/read', dict(cwd=str(workspace), includeLayers=False))
        save(output / 'effective-config.json', effective)
        require(effective['config'].get('allow_login_shell') is False, 'Login shells must be disabled')
        probe = 'const keys=' + json.dumps(PROBE_ENV_KEYS) + ';\n' + """const fs=require('node:fs'); const cp=require('node:child_process');
const file=process.env.TMPDIR+'/dev122-probe-'+process.pid;
fs.writeFileSync(file,'synthetic',{flag:'wx'});fs.unlinkSync(file);
const git=cp.spawnSync('git',['--version'],{encoding:'utf8'});
if(git.error||git.status!==0)throw git.error||new Error(git.stderr);
console.log(JSON.stringify({env:Object.fromEntries(keys.map(k=>[k,process.env[k]])),git:git.stdout.trim()}));"""
        probe_result = server.command('environment', 'node -e ' + shlex.quote(probe))
        require(probe_result['exitCode'] == 0, 'Shell environment probe failed')
        observed = json.loads(probe_result['stdout'])
        validate_environment({key: env[key] for key in PROBE_ENV_KEYS}, observed['env'])
        doctor = server.command('doctor', 'node tools/agent-harness/dist/cli.js doctor --json')
        require(doctor['exitCode'] == 0 and json.loads(doctor['stdout'])['ready'], 'Doctor failed')
        started = time.time()
        result = server.command('verify', 'node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline --timeout-ms 150000', 180000)
        summary = json.loads(result['stdout'])
        expected = dict(tests=21, failures=0 if args.expect == 'healthy' else 1, errors=0, skipped=0)
        require(result['exitCode'] == (0 if args.expect == 'healthy' else 1), 'Unexpected verify exit')
        require(summary['totals'] == expected and summary['validationScope'] == 'unit', 'Unexpected unit result')
        report_path = Path(summary['reportPath']).resolve(strict=True)
        require(report_path.is_relative_to(workspace / 'build/agent-harness') and report_path.stat().st_mtime >= started,
                'Expected a fresh case-local report')
        report = json.loads(report_path.read_text())
        require(report['junit']['totals'] == expected, 'Summary/report mismatch')
        expected_problems = [] if args.expect == 'healthy' else ['Gradle exit code: 1', 'Suite has failures, errors, skipped tests or no tests']
        require(report['problems'] == expected_problems, 'Environment or other verification failure')
        failures = []
        for path in (report_path.parent / 'junit').glob('TEST-*.xml'):
            for item in ET.parse(path).getroot().findall('testcase'):
                if item.find('failure') is not None:
                    failures.append((item.attrib['classname'], item.attrib['name']))
        if args.expect == 'T01':
            require(failures == [('com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.cache.TakenLectureCacheTest',
                                  'malformedAndForeignUserPayloadsFallBack()')], 'Wrong seeded oracle failed')
        save(output / 'result.json', dict(
            status='passed', expected=args.expect, totals=expected, reportPath=str(report_path),
            configTemplateSha256=hashlib.sha256(template.encode()).hexdigest(),
            clientVersion=version, additionalModelRuns=0,
            scope='Model-free command/exec with /bin/zsh -c; not a codex exec model trial or integration isolation proof',
        ))
    finally:
        server.close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--codex', type=Path, required=True)
    parser.add_argument('--case-root', type=Path, required=True)
    parser.add_argument('--out', type=Path, required=True)
    parser.add_argument('--expect', choices=['healthy', 'T01'], required=True)
    args = parser.parse_args()
    output = args.out.resolve()
    output.mkdir(parents=True, exist_ok=False)
    try:
        check(args, output)
    except Exception as error:
        save(output / 'result.json', dict(status='failed', error=f'{type(error).__name__}: {error}', additionalModelRuns=0))
        print(f'Runtime check failed: {error}', file=sys.stderr)
        sys.exit(1)
