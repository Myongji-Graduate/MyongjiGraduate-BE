#!/usr/bin/env python3
"""Run model-free Codex 0.166.0 command/MCP/skill checks inside a prepared DEV-122 VM."""
import argparse
import hashlib
import json
import os
from pathlib import Path
import queue
import shlex
import shutil
import signal
import subprocess
import threading
import time
import tomllib
import xml.etree.ElementTree as ET

ALLOWED_METHODS = {'initialize', 'config/read', 'account/read', 'skills/list',
                   'mcpServerStatus/list', 'command/exec'}


def require(value, message):
    if not value:
        raise RuntimeError(message)


def save(path, value):
    path.write_text(json.dumps(value, ensure_ascii=False, indent=2) + '\n')


def process_environment(pinned, inherited, state):
    env = {key: inherited[key] for key in ('HOME', 'USER', 'LOGNAME', 'SHELL', 'TERM') if key in inherited}
    env.update(pinned)
    env['CODEX_HOME'] = str(state)
    return env


def validate_effective_config(expected, observed):
    for key in ('model', 'model_reasoning_effort', 'allow_login_shell', 'web_search'):
        require(observed.get(key) == expected[key], f'Effective {key} differs from profile')
    for key, value in expected['features'].items():
        require(observed.get('features', {}).get(key) == value, f'Effective feature {key} differs')
    require(not observed.get('mcp_servers'), 'Unexpected MCP server configuration')


def validate_shell_environment(expected, observed):
    require(observed.get('environment') == expected and observed.get('credentialNames') == [],
            'Shell environment changed or contains credential variables')


def cleanup_server(server, provided_skill_directory):
    try:
        if provided_skill_directory is not None:
            shutil.rmtree(provided_skill_directory)
    finally:
        server.close()


class Server:
    def __init__(self, codex, workspace, env, output):
        self.output = output
        self.workspace = workspace
        self.sequence = 0
        self.messages = queue.Queue()
        self.calls = []
        self.stderr = (output / 'app-server.stderr.log').open('w')
        self.process = subprocess.Popen([str(codex), 'app-server', '--strict-config'],
                                        cwd=workspace, env=env, stdin=subprocess.PIPE,
                                        stdout=subprocess.PIPE, stderr=self.stderr,
                                        text=True, bufsize=1, start_new_session=True)
        self.reader = threading.Thread(target=self.read, daemon=True)
        self.reader.start()

    def read(self):
        try:
            for line in self.process.stdout:
                self.messages.put(json.loads(line))
        except Exception as error:
            self.messages.put(error)
        finally:
            self.messages.put(EOFError('app-server closed stdout'))

    def send(self, value):
        self.process.stdin.write(json.dumps(value) + '\n')
        self.process.stdin.flush()

    def call(self, method, params, timeout=30):
        require(method in ALLOWED_METHODS, 'This checker cannot start threads, turns, or model calls')
        self.sequence += 1
        self.calls.append({'id': self.sequence, 'method': method})
        save(self.output / 'rpc-methods.json', self.calls)
        self.send({'id': self.sequence, 'method': method, 'params': params})
        deadline = time.monotonic() + timeout
        while True:
            reply = self.messages.get(timeout=max(0, deadline - time.monotonic()))
            if isinstance(reply, Exception):
                raise reply
            if reply.get('id') == self.sequence:
                require('error' not in reply, f'{method}: {reply.get("error")}')
                return reply['result']
            require('id' not in reply, 'Unexpected server request')
            require(reply.get('method') not in ('thread/started', 'turn/started'), 'Unexpected model turn')

    def command(self, label, command, timeout_ms=20000):
        result = self.call('command/exec', {
            'command': ['/bin/bash', '-c', command], 'cwd': str(self.workspace),
            'sandboxPolicy': {'type': 'externalSandbox', 'networkAccess': 'restricted'},
            'timeoutMs': timeout_ms, 'outputBytesCap': 300000,
        }, timeout=timeout_ms / 1000 + 30)
        save(self.output / (label + '.json'), {'command': command, **result})
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


def enabled_skills(response):
    require(len(response['data']) == 1, 'Unexpected skill roots')
    row = response['data'][0]
    require(not row.get('errors'), 'Skill discovery error')
    return [s for s in row['skills'] if s.get('enabled', True)]


def check(args, output):
    require(os.uname().sysname == 'Linux', 'Execute only inside the prepared Linux VM')
    workspace = Path('/opt/dev122/workspace')
    state = Path('/opt/dev122/codex-state')
    require((workspace / '.git').is_dir(), 'Prepared independent Git fixture required')
    require(not (state / 'auth.json').exists() and not (state / 'auth.json').is_symlink(),
            'Real authentication is not part of this model-free checker')
    require(not (workspace / '.agents').exists(), 'Expected a skill-free fixture before this check')
    profile = (state / 'config.toml').read_bytes()
    config = tomllib.loads(profile.decode())
    require(config['allow_login_shell'] is False and config['web_search'] == 'disabled', 'Unexpected profile')
    pinned = config['shell_environment_policy']['set']
    env = process_environment(pinned, os.environ, state)
    require(not any(env.get(k) for k in ('OPENAI_API_KEY', 'CODEX_API_KEY')), 'Unexpected API credential')
    version = subprocess.check_output([str(args.codex), '--version'], env=env, text=True).strip()
    require(version == 'codex-cli 0.166.0', 'Wrong Codex client version')
    server = Server(args.codex, workspace, env, output)
    created_skills = False
    try:
        server.call('initialize', {'clientInfo': {'name': 'dev122-vm-command-check', 'version': '1'},
                                   'capabilities': {'experimentalApi': True}})
        server.send({'method': 'initialized', 'params': {}})
        effective = server.call('config/read', {'cwd': str(workspace), 'includeLayers': False})
        save(output / 'effective-config.json', effective)
        validate_effective_config(config, effective['config'])
        account = server.call('account/read', {'refreshToken': False})
        save(output / 'authentication-state.json', {'accountPresent': account.get('account') is not None})
        require(account.get('account') is None, 'Checker must remain logged out')
        mcp = server.call('mcpServerStatus/list', {'limit': 100})
        save(output / 'mcp-status.json', mcp)
        require(mcp['data'] == [] and not mcp.get('nextCursor'), 'Unexpected MCP inventory')
        before = server.call('skills/list', {'cwds': [str(workspace)], 'forceReload': True})
        save(output / 'skills-without.json', before)
        require(enabled_skills(before) == [], 'Unexpected personal or system skills')
        probe = ('const keys=' + json.dumps(list(pinned)) + ';' +
                 'const cp=require("node:child_process"); const fs=require("node:fs");' +
                 'const p=process.env.TMPDIR+"/vm-command-probe-"+process.pid;' +
                 'fs.writeFileSync(p,"synthetic",{flag:"wx"});fs.unlinkSync(p);' +
                 'const r=cp.spawnSync("git",["--version"],{encoding:"utf8"});' +
                 'if(r.error||r.status!==0)throw r.error||new Error(r.stderr);' +
                 'console.log(JSON.stringify({environment:Object.fromEntries(keys.map(k=>[k,process.env[k]])),' +
                 'credentialNames:["OPENAI_API_KEY","CODEX_API_KEY","GH_TOKEN","GITHUB_TOKEN","MCP_SENTINEL_TOKEN_NAME"].filter(k=>!!process.env[k])}));')
        observed = server.command('environment', 'node -e ' + shlex.quote(probe))
        require(observed['exitCode'] == 0, 'Shell environment probe failed')
        observed_env = json.loads(observed['stdout'])
        validate_shell_environment(pinned, observed_env)
        results = []
        for suite, count in [('takenlecture-unit', 21), ('takenlecture-integration', 31)]:
            doctor = server.command(suite + '-doctor', 'node tools/agent-harness/dist/cli.js doctor ' + suite + ' --json')
            require(doctor['exitCode'] == 0 and json.loads(doctor['stdout'])['ready'], 'Doctor failed')
            started = time.time()
            execution = server.command(suite + '-verify',
                                       'node tools/agent-harness/dist/cli.js verify ' + suite + ' --offline', 420000)
            summary = json.loads(execution['stdout'])
            report_path = Path(summary['reportPath']).resolve(strict=True)
            require(report_path.is_relative_to(workspace / 'build/agent-harness') and report_path.stat().st_mtime >= started,
                    'Report must be fresh and case-local')
            report = json.loads(report_path.read_text())
            expected = dict(tests=count, failures=0, errors=0, skipped=0)
            require(execution['exitCode'] == 0 and report['status'] == 'passed' and report['junit']['totals'] == expected,
                    'Unexpected suite result')
            require(report['codeBefore'] == report['codeAfter'] and not report['problems'], 'Code or environment changed')
            if suite == 'takenlecture-integration':
                require(report['cleanup']['confirmed'], 'Synthetic container cleanup failed')
            totals = {key: 0 for key in expected}
            for path in report_path.parent.glob('junit/TEST-*.xml'):
                xml = ET.fromstring(path.read_text())
                for key in totals:
                    totals[key] += int(xml.attrib[key])
            require(totals == expected, 'XML counts differ')
            results.append({'suite': suite, 'totals': totals, 'reportPath': str(report_path)})
        if args.skill_seed:
            seed = args.skill_seed.resolve(strict=True)
            require(seed.is_relative_to('/opt/dev122'), 'Use a prepared local skill seed')
            skill_root = workspace / '.agents/skills/mju-verify'
            created_skills = True
            shutil.copytree(seed, skill_root)
            after = server.call('skills/list', {'cwds': [str(workspace)], 'forceReload': True})
            save(output / 'skills-with.json', after)
            skills = enabled_skills(after)
            require(len(skills) == 1 and skills[0]['name'] == 'mju-verify' and
                    Path(skills[0]['path']).resolve() == skill_root / 'SKILL.md', 'Unexpected provided skill')
        result = {'status': 'command-mcp-skill-check-passed', 'suites': results,
                  'clientVersion': version, 'profileSha256': hashlib.sha256(profile).hexdigest(),
                  'modelCalls': 0, 'authenticationVerified': False,
                  'scope': 'Model-free command/exec under the external VM boundary; no thread or turn'}
    finally:
        cleanup_server(server, workspace / '.agents' if created_skills else None)
    cleanup = {'appServerTerminated': server.process.poll() is not None,
               'readerStopped': not server.reader.is_alive(),
               'providedSkillRemoved': not (workspace / '.agents').exists()}
    require(all(cleanup.values()), 'Codex checker cleanup incomplete')
    save(output / 'result.json', {**result, 'cleanup': cleanup})


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--codex', type=Path, required=True)
    parser.add_argument('--out', type=Path, required=True)
    parser.add_argument('--skill-seed', type=Path)
    args = parser.parse_args()
    require(args.out.resolve().is_relative_to('/opt/dev122'), 'Write evidence only inside the prepared VM')
    args.out.mkdir(parents=True, exist_ok=False)
    try:
        check(args, args.out)
    except Exception as error:
        save(args.out / 'error.json', {'error': repr(error), 'modelCalls': 0})
        raise
