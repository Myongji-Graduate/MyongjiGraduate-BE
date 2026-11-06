import { createHash, randomUUID } from 'node:crypto';
import { execFileSync } from 'node:child_process';
import { mkdir, writeFile, readdir, unlink } from 'node:fs/promises';
import { join } from 'node:path';
import { constants } from 'node:os';
import { readJUnit } from './junit.js';
import { runCommand, type Runner, type CommandResult } from './process.js';
import { takenLectureUnit, type VerificationSuite } from './suites.js';
import { cleanupContainers, ryukImage } from './containers.js';

export function codeState(root: string) {
  const git = (args: string[]) => execFileSync('git', args, { cwd: root, maxBuffer: 32 * 1024 * 1024, stdio: ['ignore', 'pipe', 'pipe'] });
  const diff = git(['diff', '--no-ext-diff', '--no-textconv', '--binary', 'HEAD', '--']);
  return {
    revision: git(['rev-parse', 'HEAD']).toString().trim(), trackedDirty: diff.length > 0,
    trackedDiffSha256: createHash('sha256').update(diff).digest('hex'),
    fingerprintCoverage: 'HEAD plus tracked working-tree/index diff; untracked and ignored contents are not fingerprinted',
  };
}
export interface VerifyOptions { root: string; offline: boolean; timeoutMs: number; suite?: VerificationSuite; dockerHost?: string }
export async function verify(options: VerifyOptions, runner: Runner = runCommand) {
  const suite = options.suite ?? takenLectureUnit;
  if (suite.containers && !options.dockerHost?.startsWith('unix:///')) throw new Error('Run doctor for the selected container suite first; a local Docker host is required');
  const started = new Date();
  const before = codeState(options.root);
  const runId = `${started.toISOString().replaceAll(':', '-')}-${randomUUID()}`;
  const parent = join(options.root, 'build', 'agent-harness');
  await mkdir(parent, { recursive: true });
  const output = join(parent, runId);
  await mkdir(output);
  const xmlDirectory = join(output, 'junit');
  await mkdir(xmlDirectory);
  const initPath = join(output, 'junit.init.gradle');
  const testHome = join(output, 'test-home');
  if (suite.containers) await mkdir(testHome);
  await writeFile(initPath, `allprojects { tasks.withType(Test).configureEach { ${suite.containers?.bootJar ? "dependsOn('bootJar'); " : ''}${suite.containers ? "systemProperty('user.home', System.getProperty('jolbu.test.home')); " : ''}reports.junitXml.outputLocation.set(file(System.getProperty('jolbu.junit.output'))) } }\n`, { flag: 'wx' });
  const args = ['test', ...suite.classes.flatMap(({ name }) => ['--tests', name]), '--no-daemon', '--rerun-tasks', '--no-build-cache', '--init-script', initPath, `-Djolbu.junit.output=${xmlDirectory}`, ...(suite.containers ? [`-Djolbu.test.home=${testHome}`] : []), ...(options.offline ? ['--offline'] : [])];
  const env: NodeJS.ProcessEnv = {
    RUN_TAKEN_LECTURE_CACHE_REPRODUCTION: 'false', RUN_TAKEN_LECTURE_CACHE_HTTP: 'false',
    DEV121_HTTP_BASELINE_JAR: undefined, DEV121_HTTP_STEADY: 'false',
    DEV121_MYSQL_IMAGE: 'mysql:8.4.8', DEV121_SYNTHETIC_PASSWORD: undefined,
  };
  const httpOutput = suite.validationScope === 'http' ? join(output, 'http') : null;
  if (suite.containers) Object.assign(env, {
    [suite.containers.optIn]: 'true', DOCKER_HOST: options.dockerHost, DOCKER_CONTEXT: undefined,
    TESTCONTAINERS_HOST_OVERRIDE: '127.0.0.1', TESTCONTAINERS_REUSE_ENABLE: 'false',
    TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE: process.platform === 'darwin' ? '/var/run/docker.sock' : options.dockerHost!.slice('unix://'.length),
    TESTCONTAINERS_RYUK_DISABLED: 'false', TESTCONTAINERS_RYUK_CONTAINER_IMAGE: ryukImage,
    TESTCONTAINERS_CHECKS_DISABLE: 'true', MJU_HARNESS_RUN_ID: runId, MJU_HARNESS_HTTP_OUTPUT: httpOutput ?? undefined,
  });
  let execution: CommandResult;
  try {
    execution = await runner({ executable: join(options.root, 'gradlew'), args, env, cwd: options.root, timeoutMs: options.timeoutMs, logPath: join(output, 'gradle.log') });
  } catch (error) {
    execution = { exitCode: null, signal: null, timedOut: false, stdout: '', stderr: '', spawnError: error instanceof Error ? error.message : String(error) };
  }
  let junit: Awaited<ReturnType<typeof readJUnit>> | null = null;
  const problems: string[] = [];
  let cleanup: Awaited<ReturnType<typeof cleanupContainers>> | null = null;
  if (suite.containers) {
    try {
      cleanup = await cleanupContainers(options.root, options.dockerHost!, runId, runner);
      if (!cleanup.confirmed) problems.push('Owned synthetic containers remain after cleanup');
    } catch (error) { problems.push(error instanceof Error ? error.message : String(error)); }
  }
  // Normal test teardown removes these. Also remove only this run's temporary
  // credential files after interrupted executions, keeping logs and reports.
  if (httpOutput) {
    try {
      for (const file of await readdir(httpOutput)) {
        if (file.endsWith('.properties')) await unlink(join(httpOutput, file));
      }
    } catch (error) {
      if ((error as NodeJS.ErrnoException).code !== 'ENOENT') problems.push('Could not clean temporary HTTP configuration');
    }
  }
  try { junit = await readJUnit(xmlDirectory, suite); }
  catch (error) { problems.push(error instanceof Error ? error.message : String(error)); }
  let after: ReturnType<typeof codeState> | null = null;
  try { after = codeState(options.root); }
  catch { problems.push('Could not capture repository state after verification'); }
  if (after && (before.revision !== after.revision || before.trackedDiffSha256 !== after.trackedDiffSha256)) problems.push('Tracked code changed during verification');
  if (execution.spawnError) problems.push(execution.spawnError);
  if (execution.timedOut) problems.push('Verification exceeded timeout');
  if (execution.signal) problems.push(`Gradle terminated by ${execution.signal}`);
  if (execution.exitCode !== 0) problems.push(`Gradle exit code: ${execution.exitCode}`);
  if (junit && (junit.totals.tests === 0 || junit.totals.skipped > 0 || junit.totals.failures > 0 || junit.totals.errors > 0)) problems.push('Suite has failures, errors, skipped tests or no tests');
  const passed = problems.length === 0 && junit !== null;
  const exitCode = passed ? 0 : execution.timedOut ? 124 : execution.exitCode && execution.exitCode > 0 ? execution.exitCode : execution.signal ? 128 + (constants.signals[execution.signal] ?? 1) : 1;
  const report = {
    schemaVersion: 1, toolVersion: '0.1.0', command: 'verify', suite: suite.id,
    suiteVersion: suite.version, validationScope: suite.validationScope,
    runId, status: passed ? 'passed' : execution.timedOut ? 'timed_out' : 'failed',
    startedAt: started.toISOString(), finishedAt: new Date().toISOString(), durationMs: Date.now() - started.getTime(),
    repositoryRoot: options.root, codeBefore: before, codeAfter: after,
    execution: { executable: './gradlew', args, environmentOverrides: env, offline: options.offline, timeoutMs: options.timeoutMs, exitCode: execution.exitCode, signal: execution.signal, timedOut: execution.timedOut },
    junit, cleanup, problems, limitations: suite.limitations,
    artifacts: { log: join(output, 'gradle.log'), junit: xmlDirectory, ...(httpOutput ? { http: httpOutput } : {}) },
  };
  const reportPath = join(output, 'report.json');
  await writeFile(reportPath, JSON.stringify(report, null, 2) + '\n', { flag: 'wx' });
  return { exitCode, reportPath, report };
}
