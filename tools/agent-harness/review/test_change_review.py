import copy
import importlib.util
import json
import os
from pathlib import Path
import subprocess
import tempfile
import unittest

spec = importlib.util.spec_from_file_location('change_review', Path(__file__).with_name('change-review.py'))
review = importlib.util.module_from_spec(spec)
spec.loader.exec_module(review)


class ChangeReviewTest(unittest.TestCase):
    def setUp(self):
        temp = tempfile.TemporaryDirectory()
        self.addCleanup(temp.cleanup)
        self.root = Path(temp.name).resolve()
        self.env = {**os.environ, 'GIT_CONFIG_GLOBAL': '/dev/null', 'GIT_CONFIG_NOSYSTEM': '1'}
        template = self.root / 'empty-template'; template.mkdir()
        self.git('init', '-q', '--template=' + str(template), '--initial-branch=chore/DEV-122')
        self.name = 'src/main/java/Example.java'
        self.write(self.name, 'class Example {\n    int value() { return 1; }\n}\n')
        self.write('.gitignore', 'build/\n')
        self.git('add', '.')
        self.git('-c', 'user.name=Synthetic Fixture', '-c', 'user.email=fixture@localhost',
                 '-c', 'commit.gpgsign=false', 'commit', '-qm', 'test: 합성 입력')
        self.write(self.name, 'class Example {\n    int value() { return 2; }\n}\n')
        self.out = self.root / 'build/agent-harness/review-fixture'

    def git(self, *args):
        return subprocess.check_output(['git', *args], cwd=self.root, env=self.env, stderr=subprocess.PIPE)

    def write(self, name, text):
        path = self.root / name; path.parent.mkdir(parents=True, exist_ok=True); path.write_text(text)
        return path

    def prepare(self, **kwargs):
        return review.prepare(self.root, [self.name], 'Review the synthetic change.', self.out, **kwargs)

    def report(self):
        state, _ = review.capture(self.root, [self.name])
        code = {'revision': state['head'], 'trackedDiffSha256': state['trackedDiffSha256']}
        report = {'schemaVersion': 1, 'command': 'verify', 'repositoryRoot': str(self.root),
                  'codeBefore': code, 'codeAfter': code, 'suite': 'takenlecture-unit', 'validationScope': 'unit',
                  'status': 'passed', 'junit': {
                      'suites': [{'name': 'Fixture', 'tests': 1, 'failures': 0, 'errors': 0, 'skipped': 0}],
                      'totals': {'tests': 1, 'failures': 0, 'errors': 0, 'skipped': 0}},
                  'execution': {'exitCode': 0, 'timedOut': False, 'signal': None}, 'problems': [],
                  'limitations': ['Synthetic fixture'], 'finishedAt': '2026-11-03T00:00:00Z'}
        path = self.write('build/agent-harness/verify-fixture/report.json', json.dumps(report))
        self.write('build/agent-harness/verify-fixture/junit/TEST-Fixture.xml',
                   '<testsuite name="Fixture" tests="1" failures="0" errors="0" skipped="0">'
                   '<testcase name="synthetic" classname="Fixture"/></testsuite>')
        return path, report

    def form(self):
        manifest = review.packet_manifest(self.out)
        return {'schemaVersion': 1, 'packetId': manifest['packetId'], 'actor': {
                'kind': 'model', 'modelId': None, 'humanAttestation': False},
                'summary': 'Synthetic review only', 'findings': [], 'unverified': ['No tests submitted']}

    def test_selected_tracked_and_untracked_sources_are_frozen_and_later_changes_are_stale(self):
        added = 'src/test/java/NewTest.java'
        self.write(added, '// synthetic new test\n')
        result = review.prepare(self.root, [self.name, added], 'Review', self.out)
        self.assertEqual(2, result['selectedFiles'])
        self.assertIn('return 1;', (self.out / 'reviewer/base' / self.name).read_text())
        self.assertIn('return 2;', (self.out / 'reviewer/current' / self.name).read_text())
        self.assertTrue(review.verify_state(self.root, self.out)['selectedSnapshotMatches'])
        self.write(added, '// changed after review\n')
        with self.assertRaisesRegex(ValueError, 'stale'):review.verify_state(self.root, self.out)
        with self.assertRaisesRegex(ValueError, 'Output exists'):self.prepare()

    def test_sensitive_ignored_pattern_and_symlink_inputs_are_rejected(self):
        for name in ['state/student.json', '../outside.java', 'src/main/resources/application.yml', 'docs/*.md', '.env']:
            with self.subTest(name=name), self.assertRaises(ValueError):
                review.capture(self.root, [name])
        self.write('docs/real.md', 'synthetic')
        (self.root / 'docs/link.md').symlink_to(self.root / 'docs/real.md')
        with self.assertRaisesRegex(ValueError, 'Symlinks'):review.capture(self.root, ['docs/link.md'])
        self.write('docs/.env.secret.md', 'synthetic')
        with self.assertRaises(ValueError):review.capture(self.root, ['docs/.env.secret.md'])
        self.write('.gitignore', 'build/\ndocs/ignored.md\n')
        self.write('docs/ignored.md', 'synthetic ignored source')
        with self.assertRaisesRegex(ValueError, 'Ignored input'):review.capture(self.root, ['docs/ignored.md'])

    def test_absolute_evidence_alias_cannot_bypass_symlink_guard(self):
        target = self.write('build/agent-harness/real.log', 'synthetic log')
        alias = self.root / 'build/agent-harness/alias.log'
        alias.symlink_to(target)
        with self.assertRaisesRegex(ValueError, 'Symlinks'):review.evidence_file(self.root, alias)

    def test_check_receipt_binds_selected_untracked_bytes_and_log(self):
        name = 'tools/agent-harness/review/new.py'; self.write(name, '# synthetic\n')
        state, _ = review.capture(self.root, [name])
        log = self.write('build/agent-harness/check.log', 'Synthetic tests: OK\n')
        receipt = self.write('build/agent-harness/check.json', json.dumps({
            'kind': 'selection-check-receipt', 'beforeFingerprint': state['selectionFingerprint'],
            'afterFingerprint': state['selectionFingerprint'], 'exitCode': 0, 'command': ['synthetic-check'],
            'logPath': str(log.relative_to(self.root)), 'logSha256': review.sha(log.read_bytes())}))
        review.prepare(self.root, [name], 'Review', self.out, receipts=[receipt])
        checks = json.loads((self.out / 'reviewer/evidence.json').read_text())['checks']
        self.assertTrue(checks[0]['matchesSelectedSnapshot'])
        self.assertFalse(checks[0]['executionIndependentlyVerified'])
        self.write(name, '# modified untracked bytes\n')
        with self.assertRaisesRegex(ValueError, 'receipt is stale'):
            review.prepare(self.root, [name], 'Review', self.root / 'another', receipts=[receipt])

    def test_matching_suite_report_preserves_xml_and_rejects_other_tracked_state(self):
        path, _ = self.report(); self.prepare(reports=[path])
        check = json.loads((self.out / 'reviewer/evidence.json').read_text())['checks'][0]
        self.assertTrue(check['matchesTrackedSnapshot'])
        self.assertFalse(check['selectedUntrackedCoverageVerified'])
        self.write(self.name, 'class Example { int changedAgain; }\n')
        with self.assertRaisesRegex(ValueError, 'does not match tracked state'):
            review.prepare(self.root, [self.name], 'Review', self.root / 'another', reports=[path])

    def test_passing_report_with_false_junit_counts_is_rejected(self):
        path, _ = self.report()
        self.write('build/agent-harness/verify-fixture/junit/TEST-Fixture.xml',
                   '<testsuite tests="1" failures="0" errors="0" skipped="0"><testcase name="x"><failure/></testcase></testsuite>')
        with self.assertRaisesRegex(ValueError, 'JUnit counters disagree'):self.prepare(reports=[path])
        self.assertFalse(self.out.exists())

    def test_same_totals_from_a_different_suite_are_rejected(self):
        path, _ = self.report()
        (path.parent / 'junit/TEST-Fixture.xml').unlink()
        self.write('build/agent-harness/verify-fixture/junit/TEST-Foreign.xml',
                   '<testsuite name="Foreign" tests="1" failures="0" errors="0" skipped="0">'
                   '<testcase name="synthetic" classname="Foreign"/></testsuite>')
        with self.assertRaisesRegex(ValueError, 'Report/JUnit suite mismatch'):
            self.prepare(reports=[path])

    def test_per_suite_counts_must_match_even_when_combined_totals_match(self):
        path, report = self.report()
        report['junit']['suites'][0]['tests'] = 2
        report['junit']['suites'].append({'name': 'Other', 'tests': 1, 'failures': 0, 'errors': 0, 'skipped': 0})
        report['junit']['totals']['tests'] = 3
        path.write_text(json.dumps(report))
        self.write('build/agent-harness/verify-fixture/junit/TEST-Other.xml',
                   '<testsuite name="Other" tests="2" failures="0" errors="0" skipped="0">'
                   '<testcase name="one" classname="Other"/><testcase name="two" classname="Other"/></testsuite>')
        with self.assertRaisesRegex(ValueError, 'Report/JUnit suite mismatch'):
            self.prepare(reports=[path])

    def test_xml_filename_and_testcase_identities_cannot_be_substituted(self):
        path, report = self.report()
        for label, suite, cases in [
            ('filename', 'Foreign', [('Foreign', 'one')]),
            ('classname', 'Fixture', [('Foreign', 'one')]),
            ('duplicate', 'Fixture', [('Fixture', 'same'), ('Fixture', 'same')]),
        ]:
            with self.subTest(label=label):
                report['junit']['totals']['tests'] = len(cases)
                report['junit']['suites'][0]['tests'] = len(cases)
                path.write_text(json.dumps(report))
                body = ''.join(f'<testcase classname="{cls}" name="{name}"/>' for cls, name in cases)
                self.write('build/agent-harness/verify-fixture/junit/TEST-Fixture.xml',
                           f'<testsuite name="{suite}" tests="{len(cases)}" failures="0" errors="0" skipped="0">{body}</testsuite>')
                with self.assertRaisesRegex(ValueError, 'JUnit .*identity'):
                    review.prepare(self.root, [self.name], 'Review', self.root / ('bad-' + label), reports=[path])

    def test_diff_preserves_line_boundaries_when_sources_have_no_final_lf(self):
        other = 'src/main/java/Other.java'
        self.write(self.name, 'class Example { int value = 1; }')
        self.write(other, 'class Other { int value = 1; }')
        self.git('add', self.name, other)
        self.git('-c', 'user.name=Synthetic Fixture', '-c', 'user.email=fixture@localhost',
                 '-c', 'commit.gpgsign=false', 'commit', '-qm', 'test: 개행 없는 합성 입력')
        self.write(self.name, 'class Example { int value = 2; }')
        self.write(other, 'class Other { int value = 2; }')
        review.prepare(self.root, [self.name, other], 'Review', self.out)
        diff = self.out / 'reviewer/change.diff'
        self.assertEqual(4, diff.read_text().count('\\ No newline at end of file\n'))
        self.git('apply', '--check', '--reverse', str(diff))

    def test_review_requires_real_source_line_and_discloses_missing_verification(self):
        self.prepare(); form = self.form(); path = self.root / 'model-review.json'
        form['findings'] = [{'priority': 2, 'title': 'Synthetic finding', 'body': 'Synthetic fixture only',
                             'file': self.name, 'side': 'current', 'line': 2,
                             'evidence': [{'path': 'current/' + self.name, 'quote': '    int value() { return 2; }'}]}]
        review.dump(path, form)
        self.assertEqual(1, review.validate_review(self.out, path)['findings'])
        for change in [lambda f: f['findings'][0].update(line=99),
                       lambda f: f['findings'][0].update(line=1),
                       lambda f: f['findings'][0]['evidence'][0].update(quote='invented'),
                       lambda f: f['actor'].update(humanAttestation=True),
                       lambda f: f.update(unverified=[])]:
            bad = copy.deepcopy(form); change(bad); review.dump(path, bad)
            with self.assertRaises(ValueError):review.validate_review(self.out, path)

    def test_packet_tampering_and_extra_files_are_rejected(self):
        self.prepare(); path = self.out / 'reviewer/current' / self.name; raw = path.read_bytes()
        path.write_text('tampered')
        with self.assertRaisesRegex(ValueError, 'evidence changed'):review.packet_manifest(self.out)
        path.write_bytes(raw); (self.out / 'reviewer/extra.md').write_text('unrecorded')
        with self.assertRaisesRegex(ValueError, 'file set changed'):review.packet_manifest(self.out)

    def test_deleted_source_is_reviewable_on_base_side(self):
        (self.root / self.name).unlink(); self.prepare()
        form = self.form(); form['findings'] = [{'priority': 2, 'title': 'Synthetic deleted API',
            'body': 'Synthetic fixture only', 'file': self.name, 'side': 'base', 'line': 1,
            'evidence': [{'path': 'base/' + self.name, 'quote': 'class Example {'}]}]
        path = self.root / 'review.json'; review.dump(path, form)
        self.assertEqual(1, review.validate_review(self.out, path)['findings'])

    def test_generated_diff_can_exceed_one_source_file_limit_within_packet_budget(self):
        self.write(self.name, ('a' * 54 + '\n') * 3000)
        self.git('add', self.name)
        self.git('-c', 'user.name=Synthetic Fixture', '-c', 'user.email=fixture@localhost',
                 '-c', 'commit.gpgsign=false', 'commit', '-qm', 'test: 큰 합성 입력')
        self.write(self.name, ('b' * 54 + '\n') * 3000)
        self.prepare()
        self.assertGreater((self.out / 'reviewer/change.diff').stat().st_size, review.MAX_FILE)
        self.assertTrue(review.verify_state(self.root, self.out)['selectedSnapshotMatches'])
        path = self.root / 'review.json'; review.dump(path, self.form())
        self.assertEqual('development-model-review-valid', review.validate_review(self.out, path)['status'])

    def test_verification_log_uses_packet_budget_instead_of_source_file_limit(self):
        state, _ = review.capture(self.root, [self.name])
        log = self.write('build/agent-harness/large.log', 'x' * (review.MAX_FILE + 100))
        receipt = self.write('build/agent-harness/large.json', json.dumps({
            'kind': 'selection-check-receipt', 'beforeFingerprint': state['selectionFingerprint'],
            'afterFingerprint': state['selectionFingerprint'], 'exitCode': 0, 'command': ['synthetic-check'],
            'logPath': str(log.relative_to(self.root)), 'logSha256': review.sha(log.read_bytes())}))
        self.prepare(receipts=[receipt])
        self.assertTrue(review.verify_state(self.root, self.out)['selectedSnapshotMatches'])

    def test_output_cannot_recreate_a_selected_deleted_file_as_a_directory(self):
        selected = self.root / self.name; selected.unlink()
        with self.assertRaisesRegex(ValueError, 'Output overlaps'):
            review.prepare(self.root, [self.name], 'Review', selected)
        self.assertFalse(selected.exists())


if __name__ == '__main__':unittest.main()
