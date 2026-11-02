import { access, readFile } from 'node:fs/promises';
import { constants } from 'node:fs';
import { join, resolve } from 'node:path';
import { runCommand } from './process.js';
import { suiteSourcePaths, takenLectureUnit, type VerificationSuite } from './suites.js';
import { checkDocker } from './containers.js';

export interface Check { name: string; ok: boolean; detail: string }
export async function findRepo(cwd: string): Promise<string> {
  const result = await runCommand({ executable: 'git', args: ['rev-parse', '--show-toplevel'], cwd, timeoutMs: 10_000 });
  if (result.exitCode !== 0 || result.spawnError) throw new Error('Run this command inside the MyongjiGraduate-BE Git checkout.');
  return result.stdout.trim();
}
export async function doctor(cwd: string, suite: VerificationSuite = takenLectureUnit) {
  const root = await findRepo(cwd);
  const checks: Check[] = [];
  const major = Number(process.versions.node.split('.')[0]);
  checks.push({ name: 'node', ok: major >= 20, detail: process.versions.node });
  // gradlew gives JAVA_HOME precedence over PATH (including the IBM JDK layout).
  let javaExecutable = 'java';
  if (process.env.JAVA_HOME) {
    const legacy = join(process.env.JAVA_HOME, 'jre/sh/java');
    try { await access(legacy, constants.X_OK); javaExecutable = legacy; }
    catch { javaExecutable = join(process.env.JAVA_HOME, 'bin/java'); }
  }
  const java = await runCommand({ executable: javaExecutable, args: ['-version'], cwd: root, timeoutMs: 10_000 });
  const version = (java.stdout + java.stderr).match(/version\s+"([0-9]+)(?:[.\-"])/)?.[1];
  checks.push({ name: 'java', ok: java.exitCode === 0 && version === '21', detail: version ? `Java ${version}; Java 21 required; executable: ${javaExecutable}` : `Java 21 not found at ${javaExecutable}` });
  for (const file of ['gradlew', 'gradle/wrapper/gradle-wrapper.jar', 'gradle/wrapper/gradle-wrapper.properties', 'build.gradle', ...suiteSourcePaths(suite)]) {
    try {
      await access(resolve(root, file), file === 'gradlew' ? constants.X_OK : constants.R_OK);
      checks.push({ name: file, ok: true, detail: 'available' });
    } catch { checks.push({ name: file, ok: false, detail: 'missing or inaccessible' }); }
  }
  try {
    const wrapper = await readFile(resolve(root, 'gradle/wrapper/gradle-wrapper.properties'), 'utf8');
    checks.push({ name: 'gradle-version', ok: /gradle-8\.10-bin\.zip/.test(wrapper), detail: 'Expected repository Gradle 8.10 wrapper' });
  } catch { /* Missing file is already listed. */ }
  let dockerHost: string | undefined;
  if (suite.containers) {
    try {
      const docker = await checkDocker(root, suite.containers.images);
      dockerHost = docker.host;
      checks.push(...docker.checks);
    } catch (error) { checks.push({ name: 'docker', ok: false, detail: error instanceof Error ? error.message : String(error) }); }
  }
  return {
    schemaVersion: 1, command: 'doctor', suite: suite.id, repositoryRoot: root, javaExecutable, dockerHost, ready: checks.every((check) => check.ok), checks,
    limitations: [suite.containers ? 'Docker metadata and local images only; no container/app startup or data access.' : 'No application, database or Docker access.', 'Dependency cache availability and test success are not checked.'],
  };
}
