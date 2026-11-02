import { spawn } from 'node:child_process';
import { createWriteStream } from 'node:fs';

export interface Command { executable: string; args: string[]; cwd: string; timeoutMs: number; logPath?: string; env?: NodeJS.ProcessEnv }
export interface CommandResult {
  exitCode: number | null; signal: NodeJS.Signals | null; timedOut: boolean;
  stdout: string; stderr: string; spawnError?: string;
}
export type Runner = (command: Command) => Promise<CommandResult>;

export const runCommand: Runner = (command) => new Promise((resolve) => {
  const log = command.logPath ? createWriteStream(command.logPath, { flags: 'wx' }) : undefined;
  const child = spawn(command.executable, command.args, {
    cwd: command.cwd, env: command.env ? { ...process.env, ...command.env } : process.env,
    shell: false, detached: process.platform !== 'win32', stdio: ['ignore', 'pipe', 'pipe'],
  });
  let stdout = '', stderr = '';
  let timedOut = false;
  let spawnError: string | undefined;
  let shutdown: Promise<void> | undefined;
  let interrupted: NodeJS.Signals | null = null;
  const terminate = (signal: NodeJS.Signals) => {
    if (!child.pid) return;
    try {
      if (process.platform !== 'win32') process.kill(-child.pid, signal);
      else child.kill(signal);
    } catch { /* The process may already have exited. */ }
  };
  const stop = (signal: NodeJS.Signals) => {
    terminate(signal);
    // The group leader can exit while a descendant ignores TERM. Its close
    // event must not cancel the group's final cleanup or finish the report early.
    shutdown ??= new Promise((done) => {
      setTimeout(() => { terminate('SIGKILL'); done(); }, 1_500);
    });
  };
  const onInterrupt = () => { interrupted = 'SIGINT'; stop('SIGINT'); };
  const onTerminate = () => { interrupted = 'SIGTERM'; stop('SIGTERM'); };
  process.once('SIGINT', onInterrupt);
  process.once('SIGTERM', onTerminate);
  log?.on('error', (error) => { spawnError = `Cannot write execution log: ${error.message}`; stop('SIGTERM'); });
  child.stdout.on('data', (data: Buffer) => { stdout = (stdout + data.toString()).slice(-1_000_000); log?.write(data); });
  child.stderr.on('data', (data: Buffer) => { stderr = (stderr + data.toString()).slice(-1_000_000); log?.write(data); });
  child.on('error', (error) => { spawnError = error.message; });
  const timer = setTimeout(() => {
    timedOut = true; stop('SIGTERM');
  }, command.timeoutMs);
  child.on('close', async (exitCode, signal) => {
    clearTimeout(timer);
    process.removeListener('SIGINT', onInterrupt);
    process.removeListener('SIGTERM', onTerminate);
    await shutdown;
    const result: CommandResult = { exitCode, signal: signal ?? interrupted, timedOut, stdout, stderr, ...(spawnError ? { spawnError } : {}) };
    if (log) log.end(() => resolve(result));
    else resolve(result);
  });
});
