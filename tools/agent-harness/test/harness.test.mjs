import { test } from 'node:test';
import assert from 'node:assert/strict';
import { mkdtemp, mkdir, readFile, writeFile, rm, realpath, chmod } from 'node:fs/promises';
import { tmpdir } from 'node:os';
import { join, dirname, resolve } from 'node:path';
import { fileURLToPath, pathToFileURL } from 'node:url';
import { execFileSync, spawnSync, spawn } from 'node:child_process';
import { verify, codeState } from '../dist/verify.js';
import { takenLectureUnit, takenLectureIntegration, takenLectureHttp } from '../dist/suites.js';
import { parseSuite } from '../dist/junit.js';
import { runCommand } from '../dist/process.js';
import { findRepo } from '../dist/doctor.js';
import { cleanupContainers, checkDocker, localDockerHost } from '../dist/containers.js';

const cli = resolve(dirname(fileURLToPath(import.meta.url)), '../dist/cli.js');
const success = { exitCode: 0, signal: null, timedOut: false, stdout: '', stderr: '' };

async function repository(t) {
  const root = await mkdtemp(join(tmpdir(), 'dev122 repo with spaces '));
  t.after(() => rm(root, { recursive: true, force: true }));
  const git = (...args) => execFileSync('git', args, { cwd: root, stdio: 'pipe' });
  git('init', '--quiet');
  await writeFile(join(root, 'tracked.txt'), 'baseline\n');
  git('add', 'tracked.txt');
  git('-c', 'user.name=Harness Test', '-c', 'user.email=harness@example.invalid', 'commit', '--quiet', '-m', 'fixture');
  return root;
}

function xmlFor(suite, result = 'passed') {
  const tests = suite.tests ?? suite.baselineDeclaredTests;
  const skipped = result === 'skipped' ? tests : result === 'partial-skipped' ? 1 : 0;
  const failures = result === 'failed' ? 1 : 0;
  return `<?xml version="1.0" encoding="UTF-8"?>\n<testsuite name="${suite.name}" tests="${tests}" failures="${failures}" errors="0" skipped="${skipped}">` +
    Array.from({ length: tests }, (_, i) => `<testcase name="test${i}" classname="${suite.name}">${i < skipped ? '<skipped/>' : failures && i === 0 ? '<failure message="synthetic">failed</failure>' : ''}</testcase>`).join('') +
    '<system-out><![CDATA[log contains <testcase> and <failure>]]></system-out></testsuite>';
}

function fakeRunner({ result = success, outcome = 'passed', omit = false, foreign = false, change, extra = 0 } = {}) {
  return async (command) => {
    assert.ok(command.args.includes('--rerun-tasks'));
    assert.ok(command.args.includes('--no-build-cache'));
    assert.equal(command.args.filter((arg) => arg === '--tests').length, 4);
    const directory = command.args.find((arg) => arg.startsWith('-Djolbu.junit.output=')).slice('-Djolbu.junit.output='.length);
    await writeFile(command.logPath, 'synthetic runner; no Gradle launched\n');
    for (const [i, suite] of takenLectureUnit.classes.entries()) {
      if (omit && i === 0) continue;
      await writeFile(join(directory, `TEST-${suite.name}.xml`), xmlFor({ ...suite, tests: suite.baselineDeclaredTests + extra, ...(foreign && i === 0 ? { name: 'Foreign' } : {}) }, outcome));
    }
    await change?.(command.cwd);
    return result;
  };
}

test('success aggregates 21 fresh tests and handles Git roots with spaces', async (t) => {
  const root = await repository(t);
  const nested = join(root, 'nested'); await mkdir(nested);
  assert.equal(await findRepo(nested), await realpath(root));
  const first = await verify({ root, offline: true, timeoutMs: 3000 }, fakeRunner());
  assert.equal(first.exitCode, 0);
  assert.equal(first.report.validationScope, 'unit');
  assert.deepEqual(first.report.junit.totals, { tests: 21, failures: 0, errors: 0, skipped: 0 });
  assert.ok(first.report.execution.args.includes('--offline'));
  const saved = JSON.parse(await readFile(first.reportPath, 'utf8'));
  assert.equal(saved.status, 'passed');
  assert.match(saved.codeBefore.fingerprintCoverage, /untracked.*not fingerprinted/);
  const second = await verify({ root, offline: false, timeoutMs: 3000 }, fakeRunner({ omit: true }));
  assert.notEqual(first.reportPath, second.reportPath);
  assert.notEqual(second.exitCode, 0);
  assert.match(second.report.problems.join(' '), /fresh XML/);
});

for (const [name, setup, expected] of [
  ['nonzero Gradle status wins over passing XML', { result: { ...success, exitCode: 7 } }, 7],
  ['timeout wins over passing XML', { result: { ...success, exitCode: null, signal: 'SIGTERM', timedOut: true } }, 124],
  ['signal is retained', { result: { ...success, exitCode: null, signal: 'SIGTERM' } }, 143],
  ['all skipped never passes', { outcome: 'skipped' }, 1],
  ['even partially skipped suites do not pass', { outcome: 'partial-skipped' }, 1],
  ['JUnit failure never passes', { outcome: 'failed' }, 1],
  ['foreign suite never passes', { foreign: true }, 1],
  ['missing suite never passes', { omit: true }, 1],
]) {
  test(name, async (t) => {
    const root = await repository(t);
    const result = await verify({ root, offline: false, timeoutMs: 3000 }, fakeRunner(setup));
    assert.equal(result.exitCode, expected);
    assert.notEqual(result.report.status, 'passed');
  });
}

test('test growth is accepted without freezing the total to 21', async (t) => {
  const root = await repository(t);
  const result = await verify({ root, offline: true, timeoutMs: 3000 }, fakeRunner({ extra: 1 }));
  assert.equal(result.exitCode, 0);
  assert.equal(result.report.junit.totals.tests, 25);
});

test('no XML and process launch errors are reported', async (t) => {
  const root = await repository(t);
  const result = await verify({ root, offline: true, timeoutMs: 3000 }, async () => { throw new Error('cannot spawn'); });
  assert.notEqual(result.exitCode, 0);
  assert.match(result.report.problems.join(' '), /cannot spawn/);
  assert.match(result.report.problems.join(' '), /found 0/);
});

test('tracked edits during verification invalidate evidence; untracked content is excluded', async (t) => {
  const root = await repository(t);
  const baseline = codeState(root);
  await writeFile(join(root, 'untracked-private.txt'), 'not part of fingerprint');
  assert.deepEqual(codeState(root), baseline);
  const result = await verify({ root, offline: false, timeoutMs: 3000 }, fakeRunner({ change: (cwd) => writeFile(join(cwd, 'tracked.txt'), 'changed') }));
  assert.notEqual(result.exitCode, 0);
  assert.match(result.report.problems.join(' '), /Tracked code changed/);
  assert.notEqual(result.report.codeBefore.trackedDiffSha256, result.report.codeAfter.trackedDiffSha256);
});

test('XML rejects truncated, contradictory, duplicate and foreign test identities', () => {
  const suite = takenLectureUnit.classes[2];
  const valid = xmlFor(suite);
  assert.equal(parseSuite(valid).tests, 1);
  assert.throws(() => parseSuite(valid.replace('</testcase>', '')));
  assert.throws(() => parseSuite(valid.replace('tests="1"', 'tests="0"')));
  assert.throws(() => parseSuite(valid.replace('failures="0"', 'failures="1"')));
  assert.throws(() => parseSuite(valid.replace(`classname="${suite.name}"`, 'classname="Foreign"')));
  assert.throws(() => parseSuite(valid.replace('tests="1"', 'tests="1" tests="1"')));
  assert.throws(() => parseSuite('<!DOCTYPE testsuite>' + valid));
});

test('runner captures nonzero output and terminates a timed-out process', async (t) => {
  const root = await repository(t);
  const failed = await runCommand({ executable: process.execPath, args: ['-e', 'console.log("fixture");process.exit(9)'], cwd: root, timeoutMs: 3000, logPath: join(root, 'process.log') });
  assert.equal(failed.exitCode, 9);
  assert.match(failed.stdout, /fixture/);
  assert.match(await readFile(join(root, 'process.log'), 'utf8'), /fixture/);
  const timedOut = await runCommand({ executable: process.execPath, args: ['-e', 'setInterval(()=>{},1000)'], cwd: root, timeoutMs: 100 });
  assert.equal(timedOut.timedOut, true);
  assert.notEqual(timedOut.exitCode, 0);
});

test('runner forwards termination to its owned child and returns a non-success signal', { timeout: 8000 }, async (t) => {
  const root = await repository(t);
  const module = pathToFileURL(resolve(dirname(fileURLToPath(import.meta.url)), '../dist/process.js')).href;
  const source = `import {runCommand} from ${JSON.stringify(module)}; const result=runCommand({executable:process.execPath,args:['-e','setInterval(()=>{},1000)'],cwd:process.cwd(),timeoutMs:5000}); console.log('ready'); console.log(JSON.stringify(await result));`;
  const child = spawn(process.execPath, ['--input-type=module', '-e', source], { cwd: root, stdio: ['ignore', 'pipe', 'pipe'] });
  t.after(() => { if (child.exitCode === null) child.kill('SIGKILL'); });
  let stdout = '', sent = false;
  child.stdout.on('data', (data) => {
    stdout += data.toString();
    if (!sent && stdout.includes('ready\n')) { sent = true; child.kill('SIGTERM'); }
  });
  await new Promise((resolve, reject) => { child.once('error', reject); child.once('close', resolve); });
  const result = JSON.parse(stdout.trim().split('\n').at(-1));
  assert.equal(result.signal, 'SIGTERM');
  assert.equal(result.timedOut, false);
});

test('timeout kills a same-group descendant after its launcher already closed', { timeout: 10000, skip: process.platform === 'win32' }, async (t) => {
  const root = await repository(t);
  const pidFile = join(root, 'descendant.pid');
  const heartbeat = join(root, 'heartbeat');
  const descendant = `process.on('SIGTERM',()=>{});const fs=require('node:fs');const target=${JSON.stringify(heartbeat)};let i=0;const beat=()=>{fs.writeFileSync(target+'.next',String(++i));fs.renameSync(target+'.next',target)};beat();fs.writeFileSync(${JSON.stringify(pidFile)},String(process.pid));setInterval(beat,25);`;
  const launcher = `require('node:child_process').spawn(process.execPath,['-e',${JSON.stringify(descendant)}],{stdio:'ignore'});setInterval(()=>{},1000);`;
  let pid;
  t.after(() => { if (pid) { try { process.kill(pid, 'SIGKILL'); } catch {} } });
  const started = Date.now();
  const result = await runCommand({ executable: process.execPath, args: ['-e', launcher], cwd: root, timeoutMs: 1500 });
  pid = Number(await readFile(pidFile, 'utf8'));
  assert.equal(result.timedOut, true);
  assert.ok(Date.now() - started >= 2900, 'awaits group cleanup after launcher close');
  // Check observable activity without requiring OS-wide process inspection.
  const stoppedAt = await readFile(heartbeat, 'utf8');
  assert.ok(Number(stoppedAt) > 0);
  await new Promise((resolve) => setTimeout(resolve, 150));
  assert.equal(await readFile(heartbeat, 'utf8'), stoppedAt, 'descendant kept running after timeout cleanup');
});

test('CLI help/list work outside repository and invalid input fails before doctor', async (t) => {
  const root = await repository(t);
  const call = (...args) => spawnSync(process.execPath, [cli, ...args], { cwd: root, encoding: 'utf8' });
  assert.equal(call('help').status, 0);
  assert.equal(JSON.parse(call('list', '--json').stdout).suites[0].id, 'takenlecture-unit');
  for (const args of [['verify', 'unknown'], ['verify', 'takenlecture-unit', '--timeout-ms', '0'], ['verify', 'takenlecture-unit', '--shell', 'anything'], ['doctor', '--execute']]) assert.equal(call(...args).status, 2);
  const result = call('doctor', '--json');
  assert.equal(result.status, 1);
  assert.equal(JSON.parse(result.stdout).ready, false);
  assert.ok(JSON.parse(result.stdout).checks.some((check) => !check.ok && check.name === 'gradlew'));
});

test('doctor matches gradlew JAVA_HOME precedence and rejects an invalid JAVA_HOME', async (t) => {
  const root = await repository(t);
  const jdk = join(root, 'fake jdk'), bin = join(root, 'fake path');
  await mkdir(join(jdk, 'bin'), { recursive: true }); await mkdir(bin);
  await writeFile(join(jdk, 'bin/java'), '#!/bin/sh\necho \'openjdk version "21.0.1"\' >&2\n');
  await chmod(join(jdk, 'bin/java'), 0o755);
  await writeFile(join(bin, 'java'), '#!/bin/sh\nexit 99\n'); await chmod(join(bin, 'java'), 0o755);
  const call = (javaHome) => spawnSync(process.execPath, [cli, 'doctor', '--json'], {
    cwd: root, encoding: 'utf8', env: { ...process.env, JAVA_HOME: javaHome, PATH: `${bin}:${process.env.PATH}` },
  });
  const good = JSON.parse(call(jdk).stdout);
  assert.equal(good.javaExecutable, join(jdk, 'bin/java'));
  assert.equal(good.checks.find((check) => check.name === 'java').ok, true);
  const bad = JSON.parse(call(join(root, 'absent')).stdout);
  assert.equal(bad.checks.find((check) => check.name === 'java').ok, false);
  assert.equal(bad.ready, false);
});

test('container suites select exact tests, opt-ins and fresh HTTP JAR; foreign/skipped evidence fails', async (t) => {
  const root = await repository(t);
  for (const suite of [takenLectureIntegration, takenLectureHttp]) {
    for (const outcome of ['passed', 'skipped', 'foreign']) {
      const result = await verify({ root, suite, dockerHost: 'unix:///test/docker.sock', offline: true, timeoutMs: 3000 }, async (command) => {
        if (command.executable === 'docker') {
          assert.ok(command.args.includes(`label=mju.harness.run=${command.args.at(-1).split('=').at(-1)}`));
          return success;
        }
        assert.equal(command.args.filter((arg) => arg === '--tests').length, 1);
        assert.ok(command.args.includes(suite.classes[0].name));
        assert.equal(command.env[suite.containers.optIn], 'true');
        const other = suite === takenLectureHttp ? 'RUN_TAKEN_LECTURE_CACHE_REPRODUCTION' : 'RUN_TAKEN_LECTURE_CACHE_HTTP';
        assert.equal(command.env[other], 'false');
        assert.equal(command.env.DEV121_HTTP_BASELINE_JAR, undefined);
        assert.equal(command.env.DEV121_HTTP_STEADY, 'false');
        const init = await readFile(command.args[command.args.indexOf('--init-script') + 1], 'utf8');
        assert.equal(init.includes("dependsOn('bootJar')"), suite === takenLectureHttp);
        assert.ok(init.includes("systemProperty('user.home'"));
        assert.ok(command.args.some((arg) => arg.startsWith('-Djolbu.test.home=')));
        const directory = command.args.find((arg) => arg.startsWith('-Djolbu.junit.output=')).split('=').slice(1).join('=');
        const selected = outcome === 'foreign' ? takenLectureUnit.classes[0] : suite.classes[0];
        await writeFile(join(directory, `TEST-${selected.name}.xml`), xmlFor(selected, outcome));
        if (suite === takenLectureHttp) {
          await mkdir(command.env.MJU_HARNESS_HTTP_OUTPUT);
          await writeFile(join(command.env.MJU_HARNESS_HTTP_OUTPUT, 'synthetic.properties'), 'temporary credentials');
        }
        return success;
      });
      assert.equal(result.exitCode === 0, outcome === 'passed');
      assert.equal(result.report.validationScope, suite.validationScope);
      assert.equal(result.report.cleanup.confirmed, true);
      if (suite === takenLectureHttp) await assert.rejects(readFile(join(result.report.artifacts.http, 'synthetic.properties')));
    }
  }
});

test('cleanup targets only run-labelled container IDs and fails closed when inspection fails', async () => {
  const owned = 'a'.repeat(64), seen = [];
  let reads = 0;
  const result = await cleanupContainers('/tmp', 'unix:///test/docker.sock', 'run-123', async (command) => {
    seen.push(command.args);
    return { ...success, stdout: command.args.includes('ps') && reads++ === 0 ? owned + '\n' : '' };
  });
  assert.equal(result.confirmed, true);
  assert.deepEqual(result.removed, [owned]);
  assert.deepEqual(seen[1], ['--host', 'unix:///test/docker.sock', 'rm', '--force', '--volumes', owned]);
  for (const args of [seen[0], seen[2]]) assert.equal(args.at(-1), 'label=mju.harness.run=run-123');
  await assert.rejects(cleanupContainers('/tmp', 'unix:///test/docker.sock', 'run-123', async () => ({ ...success, exitCode: 1 })), /confirm/);
  await assert.rejects(cleanupContainers('/tmp', 'unix:///test/docker.sock', 'run-123', async () => ({ ...success, stdout: 'invalid-id' })), /identity/);
});

test('unconfirmed container cleanup invalidates otherwise passing XML', async (t) => {
  const root = await repository(t), suite = takenLectureIntegration;
  const result = await verify({ root, suite, dockerHost: 'unix:///test/docker.sock', offline: true, timeoutMs: 3000 }, async (command) => {
    if (command.executable === 'docker') return { ...success, exitCode: 1 };
    const directory = command.args.find((arg) => arg.startsWith('-Djolbu.junit.output=')).slice('-Djolbu.junit.output='.length);
    await writeFile(join(directory, `TEST-${suite.classes[0].name}.xml`), xmlFor(suite.classes[0]));
    return success;
  });
  assert.notEqual(result.exitCode, 0);
  assert.match(result.report.problems.join(' '), /cleanup/);
});

test('Docker prerequisites reject remote endpoints and missing images without mutations', async () => {
  const original = { host: process.env.DOCKER_HOST, context: process.env.DOCKER_CONTEXT };
  delete process.env.DOCKER_CONTEXT;
  try {
    process.env.DOCKER_HOST = 'tcp://remote:2375';
    await assert.rejects(localDockerHost('/tmp'), /local/);
    process.env.DOCKER_HOST = 'unix:///test/docker.sock';
    const result = await checkDocker('/tmp', ['mysql:8.4.8'], async (command) => {
      assert.ok(command.args.includes('version') || command.args.includes('inspect'));
      assert.ok(!command.args.includes('pull'));
      return command.args.includes('mysql:8.4.8') ? { ...success, exitCode: 1 } : { ...success, stdout: 'fixture' };
    });
    assert.equal(result.checks.find((check) => check.name === 'image:mysql:8.4.8').ok, false);
  } finally {
    if (original.host === undefined) delete process.env.DOCKER_HOST; else process.env.DOCKER_HOST = original.host;
    if (original.context === undefined) delete process.env.DOCKER_CONTEXT; else process.env.DOCKER_CONTEXT = original.context;
  }
});

test('runner applies controlled environment and removes inherited overrides', async (t) => {
  const root = await repository(t);
  process.env.DEV122_TEST_INHERITED = 'present';
  try {
    const result = await runCommand({ executable: process.execPath, cwd: root, timeoutMs: 3000,
      env: { DEV122_TEST_INHERITED: undefined, DEV122_TEST_CONTROLLED: 'selected' },
      args: ['-e', 'console.log(JSON.stringify([process.env.DEV122_TEST_INHERITED ?? null,process.env.DEV122_TEST_CONTROLLED]))'] });
    assert.deepEqual(JSON.parse(result.stdout), [null, 'selected']);
  } finally { delete process.env.DEV122_TEST_INHERITED; }
});
