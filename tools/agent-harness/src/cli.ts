#!/usr/bin/env node
import { doctor } from './doctor.js';
import { suites, getSuite, takenLectureUnit } from './suites.js';
import { verify } from './verify.js';

const usage = `jolbu-dev — repository verification (no runtime dependencies)

  doctor [suite] [--json]                 Check runtime, sources and selected prerequisites
  list [--json]                           List fixed suites and their scope
  verify <suite> [--offline] [--timeout-ms N]
  help

verify writes fresh reports under build/agent-harness/<run>/.
Default timeout: 600000 ms (HTTP: 1200000); maximum: 1800000 ms.
Without --offline, Gradle may fetch dependencies. Container suites require local images.
No suite proves browser behavior or Skill effectiveness.
`;
async function main(args: string[]) {
  const command = args.shift();
  if (!command || command === 'help' || command === '--help') {
    if (args.length) throw new Error('help takes no arguments');
    console.log(usage); return;
  }
  if (command === 'list' || command === 'doctor') {
    const selected = command === 'doctor' && args[0] && !args[0].startsWith('--') ? getSuite(args.shift()!) : takenLectureUnit;
    if (args.length > 1 || (args.length === 1 && args[0] !== '--json')) throw new Error('Only --json is supported');
    if (command === 'list') { console.log(JSON.stringify({ schemaVersion: 1, suites }, null, 2)); return; }
    const result = await doctor(process.cwd(), selected);
    console.log(JSON.stringify(result, null, 2)); process.exitCode = result.ready ? 0 : 1; return;
  }
  if (command !== 'verify') throw new Error('Unknown command. Use help or list.');
  const suite = getSuite(args.shift() ?? '');
  let offline = false, timeoutMs = suite.validationScope === 'http' ? 1_200_000 : 600_000;
  const seen = new Set<string>();
  while (args.length) {
    const option = args.shift()!;
    if (seen.has(option)) throw new Error(`Repeated option: ${option}`);
    seen.add(option);
    if (option === '--offline') offline = true;
    else if (option === '--timeout-ms') {
      const value = args.shift() ?? '';
      if (!/^\d+$/.test(value) || Number(value) < 1 || Number(value) > 1_800_000) throw new Error('timeout-ms must be between 1 and 1800000');
      timeoutMs = Number(value);
    } else throw new Error(`Unknown option: ${option}`);
  }
  const prerequisites = await doctor(process.cwd(), suite);
  if (!prerequisites.ready) { console.error(JSON.stringify(prerequisites, null, 2)); process.exitCode = 1; return; }
  const result = await verify({ root: prerequisites.repositoryRoot, offline, timeoutMs, suite, ...(prerequisites.dockerHost ? { dockerHost: prerequisites.dockerHost } : {}) });
  console.log(JSON.stringify({ status: result.report.status, validationScope: result.report.validationScope, totals: result.report.junit?.totals ?? null, problems: result.report.problems, reportPath: result.reportPath }, null, 2));
  process.exitCode = result.exitCode;
}
main(process.argv.slice(2)).catch((error: unknown) => {
  console.error(error instanceof Error ? error.message : String(error)); process.exitCode = 2;
});
