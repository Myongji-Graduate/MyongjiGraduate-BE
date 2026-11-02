// Read-only preparation check. This does not apply mutations or run an agent.
import assert from 'node:assert/strict';
import { createHash } from 'node:crypto';
import { readFile } from 'node:fs/promises';
import { resolve, relative, isAbsolute } from 'node:path';
import { fileURLToPath } from 'node:url';

const here = fileURLToPath(new URL('.', import.meta.url));
const root = resolve(here, '../../..');
const hash = (value) => createHash('sha256').update(value).digest('hex');
const local = (base, path) => {
  assert.equal(typeof path, 'string');
  const resolved = resolve(base, path);
  const rel = relative(base, resolved);
  assert(rel && !rel.startsWith('..') && !isAbsolute(rel), `Unsafe path: ${path}`);
  return resolved;
};
const raw = await readFile(resolve(here, 'tasks.json'), 'utf8');
const manifest = JSON.parse(raw);
assert.equal(manifest.schemaVersion, 1);
assert.equal(manifest.tasks.length, 5);
assert.deepEqual(manifest.tasks.map(({ id }) => id), ['T01', 'T02', 'T03', 'T04', 'T05']);
assert.deepEqual(manifest.conditions, ['without-skill', 'with-skill']);
assert.equal(manifest.main.repetitionsPerCondition, 3);
assert(manifest.tasks.some(({ id }) => id === manifest.pilot.taskId));
assert.equal(manifest.pilot.includedInMainResults, false);
const common = await readFile(resolve(here, 'prompts/common.md'), 'utf8');
const tasks = [];
for (const task of manifest.tasks) {
  assert([900, 1200].includes(task.wallTimeSeconds));
  assert(task.source.startsWith('src/main/java/') && task.source.endsWith('.java'));
  assert(task.oracle.testFile.startsWith('src/test/java/') && task.oracle.testFile.endsWith('.java'));
  const source = await readFile(local(root, task.source), 'utf8');
  const test = await readFile(local(root, task.oracle.testFile), 'utf8');
  const prompt = await readFile(local(here, task.promptFile), 'utf8');
  const { before, after } = task.mutation;
  assert(typeof before === 'string' && before.length > 0 && typeof after === 'string');
  assert.notEqual(before, after);
  assert.equal(source.split(before).length - 1, 1, `${task.id}: source anchor drift`);
  assert(prompt.includes(task.source), `${task.id}: missing allowed path`);
  assert(task.requiredSuites.length > 0 && new Set(task.requiredSuites).size === task.requiredSuites.length);
  assert(task.requiredSuites.every((suite) => ['takenlecture-unit', 'takenlecture-integration'].includes(suite)));
  assert(task.oracle.mustFailWhenSeeded.length > 0);
  for (const method of task.oracle.mustFailWhenSeeded) {
    assert(test.includes(`void ${method}(`), `${task.id}: missing oracle method ${method}`);
    assert(!prompt.includes(method), `${task.id}: oracle leaked into prompt`);
  }
  tasks.push({ id: task.id, sourceSha256: hash(source), seededSourceSha256: hash(source.replace(before, after)),
    promptSha256: hash(`${common}\n${prompt}`), oracleFileSha256: hash(test) });
}
const mainRuns = tasks.length * manifest.conditions.length * manifest.main.repetitionsPerCondition;
const maxMainWallMinutes = manifest.tasks.reduce((sum, task) => sum + task.wallTimeSeconds / 60, 0)
  * manifest.conditions.length * manifest.main.repetitionsPerCondition;
console.log(JSON.stringify({ status: 'preparation-valid', agentRunsExecuted: 0,
  experimentReady: false, note: 'Static anchors and prompt checks only; freeze and isolation gates are separate.',
  manifestSha256: hash(raw), mainRuns, maxMainWallMinutes, tasks }, null, 2));
