import { readdir, readFile } from 'node:fs/promises';
import { join } from 'node:path';
import { takenLectureUnit, type VerificationSuite } from './suites.js';

export interface SuiteResult { name: string; tests: number; failures: number; errors: number; skipped: number }
function attributes(tag: string): Record<string, string> {
  if (tag.replace(/([\w:-]+)="[^"]*"/g, '').replace(/\/$/, '').trim()) throw new Error('Malformed XML attributes');
  const found: Record<string, string> = {};
  for (const match of tag.matchAll(/([\w:-]+)="([^"]*)"/g)) {
    const key = match[1]!;
    if (key in found) throw new Error(`Duplicate XML attribute: ${key}`);
    found[key] = match[2]!;
  }
  return found;
}
export function parseSuite(xml: string): SuiteResult {
  // This accepts Gradle's single-suite output, not arbitrary XML documents.
  const structural = xml.replace(/<!\[CDATA\[[\s\S]*?\]\]>/g, '').replace(/<!--[\s\S]*?-->/g, '').trim();
  if (/<!DOCTYPE|<!ENTITY/i.test(structural)) throw new Error('DTD/entity declarations are not accepted');
  const stack: string[] = [];
  for (const token of structural.matchAll(/<([^>]+)>/g)) {
    const value = token[1]!;
    if (value.startsWith('?xml ')) continue;
    const name = value.match(/^\/?([\w:-]+)/)?.[1];
    if (!name) throw new Error('Malformed XML element');
    if (value.startsWith('/')) {
      if (value !== `/${name}` || stack.pop() !== name) throw new Error('Mismatched XML elements');
    } else if (!value.endsWith('/')) stack.push(name);
  }
  if (stack.length || structural.replace(/<[^>]+>/g, '').includes('<')) throw new Error('Incomplete XML');
  const root = structural.match(/^(?:<\?xml[^?]*\?>\s*)?<testsuite\b([^>]*)>[\s\S]*<\/testsuite>$/);
  if (!root || (structural.match(/<testsuite\b/g) ?? []).length !== 1) throw new Error('Expected one complete Gradle testsuite');
  const attrs = attributes(root[1]!);
  if (!attrs.name) throw new Error('Missing suite name');
  const count = (key: string): number => {
    if (!/^\d+$/.test(attrs[key] ?? '')) throw new Error(`Invalid ${key} count`);
    const value = Number(attrs[key]);
    if (!Number.isSafeInteger(value)) throw new Error(`Invalid ${key} count`);
    return value;
  };
  const result = { name: attrs.name, tests: count('tests'), failures: count('failures'), errors: count('errors'), skipped: count('skipped') };
  const cases = [...structural.matchAll(/<testcase\b([^>]*)>/g)];
  if (cases.length !== result.tests) throw new Error('Testcase count does not match suite count');
  const names = new Set<string>();
  for (const test of cases) {
    const data = attributes(test[1]!);
    if (data.classname !== result.name) throw new Error('Foreign or missing testcase class');
    if (!data.name || names.has(data.name)) throw new Error('Duplicate or missing testcase name');
    names.add(data.name);
  }
  for (const [attribute, element] of [['failures', 'failure'], ['errors', 'error'], ['skipped', 'skipped']] as const) {
    if ((structural.match(new RegExp(`<${element}\\b`, 'g')) ?? []).length !== result[attribute]) throw new Error(`${attribute} elements do not match count`);
  }
  if (result.failures + result.errors + result.skipped > result.tests) throw new Error('Outcome counts exceed tests');
  return result;
}
export async function readJUnit(directory: string, selected: VerificationSuite = takenLectureUnit) {
  const entries = await readdir(directory, { withFileTypes: true });
  const files = entries.filter((entry) => entry.name.endsWith('.xml'));
  if (files.length !== selected.classes.length) throw new Error(`Expected exactly ${selected.classes.length} fresh XML suites; found ${files.length}`);
  const suites: SuiteResult[] = [];
  for (const file of files) {
    if (!file.isFile()) throw new Error('JUnit files must be regular files');
    const suite = parseSuite(await readFile(join(directory, file.name), 'utf8'));
    const expected = selected.classes.find((item) => item.name === suite.name);
    if (!expected || suites.some((item) => item.name === suite.name)) throw new Error(`Unexpected or duplicate suite: ${suite.name}`);
    if (file.name !== `TEST-${suite.name}.xml`) throw new Error('JUnit filename does not match class identity');
    if (suite.tests === 0) throw new Error(`No tests in ${suite.name}`);
    suites.push(suite);
  }
  const totals = suites.reduce((sum, suite) => ({ tests: sum.tests + suite.tests, failures: sum.failures + suite.failures, errors: sum.errors + suite.errors, skipped: sum.skipped + suite.skipped }), { tests: 0, failures: 0, errors: 0, skipped: 0 });
  return { suites, totals };
}
