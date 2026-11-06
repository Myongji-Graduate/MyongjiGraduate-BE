import { runCommand, type Runner } from './process.js';

export const ryukImage = 'testcontainers/ryuk:0.13.0';
export const runLabel = 'mju.harness.run';
// Resolve the same endpoint for Docker CLI and Testcontainers. Remote Docker
// would invalidate loopback assumptions in the synthetic fixtures.
export async function localDockerHost(root: string, runner: Runner = runCommand): Promise<string> {
  let endpoint = process.env.DOCKER_HOST;
  if (process.env.DOCKER_CONTEXT || !endpoint) {
    const result = await runner({ executable: 'docker', args: ['context', 'inspect', '--format', '{{.Endpoints.docker.Host}}', ...(process.env.DOCKER_CONTEXT ? [process.env.DOCKER_CONTEXT] : [])], cwd: root, timeoutMs: 10_000 });
    if (result.exitCode !== 0 || result.spawnError || result.timedOut) throw new Error('Cannot inspect Docker context');
    endpoint = result.stdout.trim();
  }
  if (!endpoint?.startsWith('unix:///')) throw new Error('Synthetic suites require a local unix:// Docker socket');
  return endpoint;
}
export function dockerCommand(root: string, host: string, args: string[]) {
  return { executable: 'docker', args: ['--host', host, ...args], cwd: root, timeoutMs: 15_000,
    env: { DOCKER_CONTEXT: undefined, DOCKER_HOST: host } };
}
export async function checkDocker(root: string, images: readonly string[], runner: Runner = runCommand) {
  const host = await localDockerHost(root, runner);
  const checks: { name: string; ok: boolean; detail: string }[] = [];
  for (const [name, args] of [
    ['docker', ['version', '--format', '{{.Server.Version}}']],
    ...[...images, ryukImage].map((image) => [`image:${image}`, ['image', 'inspect', '--format', '{{.Id}}', image]]),
  ] as [string, string[]][]) {
    const result = await runner(dockerCommand(root, host, args));
    const ok = result.exitCode === 0 && !result.spawnError && !result.timedOut && !result.signal;
    checks.push({ name, ok, detail: ok ? result.stdout.trim() : 'Unavailable locally; doctor does not pull images or start containers' });
  }
  return { host, checks };
}
export async function cleanupContainers(root: string, host: string, runId: string, runner: Runner = runCommand) {
  if (!/^[a-zA-Z0-9.-]+$/.test(runId)) throw new Error('Invalid run identity');
  const list = async () => {
    const result = await runner(dockerCommand(root, host, ['ps', '-aq', '--no-trunc', '--filter', `label=${runLabel}=${runId}`]));
    if (result.exitCode !== 0 || result.spawnError || result.timedOut || result.signal) throw new Error('Cannot confirm owned container cleanup');
    const ids = result.stdout.trim().split(/\s+/).filter(Boolean);
    if (ids.some((id) => !/^[a-f0-9]{64}$/.test(id))) throw new Error('Unexpected Docker container identity');
    return ids;
  };
  const found = await list();
  if (found.length) {
    const result = await runner(dockerCommand(root, host, ['rm', '--force', '--volumes', ...found]));
    if (result.exitCode !== 0 || result.spawnError || result.timedOut || result.signal) throw new Error('Could not remove owned synthetic containers');
  }
  const remaining = await list();
  return { confirmed: remaining.length === 0, removed: found, remaining,
    scope: `Only containers with ${runLabel}=${runId} and their anonymous volumes; Testcontainers owns its resource reaper` };
}
