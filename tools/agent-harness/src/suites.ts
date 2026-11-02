const base = 'com.plzgraduate.myongjigraduatebe.takenlecture';
export interface VerificationSuite {
  id: string; version: number; validationScope: 'unit' | 'integration' | 'http'; baselineRevision: string;
  classes: readonly { name: string; baselineDeclaredTests: number }[];
  limitations: readonly string[];
  containers?: { images: readonly string[]; optIn: string; bootJar: boolean };
}
export const takenLectureUnit: VerificationSuite = {
  id: 'takenlecture-unit', version: 1, validationScope: 'unit', baselineRevision: '861228e42f779afa8a517fd3ed05a6bfafc46906',
  classes: [
    { name: `${base}.infrastructure.adapter.cache.TakenLectureCacheTest`, baselineDeclaredTests: 15 },
    { name: `${base}.application.service.delete.DeleteTakenLectureServiceTest`, baselineDeclaredTests: 2 },
    { name: `${base}.application.service.save.GenerateTakenLectureServiceTest`, baselineDeclaredTests: 1 },
    { name: `${base}.application.service.find.FindTakenLectureServiceTest`, baselineDeclaredTests: 3 },
  ],
  limitations: [
    'Direct construction and Mockito only; no actual database, Redis, HTTP server or browser.',
    'Add/delete service tests verify persistence calls, not committed revisions or final graduation results.',
    'A passing run is not end-to-end verification or evidence that the skill improves agent performance.',
  ],
};
export const takenLectureIntegration: VerificationSuite = {
  id: 'takenlecture-integration', version: 1, validationScope: 'integration', baselineRevision: takenLectureUnit.baselineRevision,
  classes: [{ name: `${base}.infrastructure.adapter.persistence.TakenLectureCacheConsistencyIntegrationTest`, baselineDeclaredTests: 31 }],
  containers: { images: ['mysql:8.4.8', 'redis:7.4-alpine'], optIn: 'RUN_TAKEN_LECTURE_CACHE_REPRODUCTION', bootJar: false },
  limitations: ['Disposable synthetic MySQL/Redis and application contexts; no HTTP or browser.', 'Synthetic fixtures do not certify all graduation policies or Skill effectiveness.'],
};
export const takenLectureHttp: VerificationSuite = {
  id: 'takenlecture-http', version: 1, validationScope: 'http', baselineRevision: takenLectureUnit.baselineRevision,
  classes: [{ name: `${base}.infrastructure.adapter.persistence.TakenLectureCacheHttpIntegrationTest`, baselineDeclaredTests: 1 }],
  containers: { images: ['mysql:8.4.8', 'redis:7.4.11-alpine'], optIn: 'RUN_TAKEN_LECTURE_CACHE_HTTP', bootJar: true },
  limitations: ['Production JAR over authenticated loopback HTTP with synthetic MySQL/Redis; no browser.', 'One JUnit scenario includes multiple HTTP checks and measurements; request count is not test count.', 'Single synthetic major/cohort; neither full academic-policy coverage nor Skill-effectiveness evidence.'],
};
export const suites = [takenLectureUnit, takenLectureIntegration, takenLectureHttp];
export function getSuite(id: string): VerificationSuite {
  const suite = suites.find((item) => item.id === id);
  if (!suite) throw new Error(`Unknown suite: ${id}. Use list.`);
  return suite;
}
export const suiteSourcePaths = (suite: VerificationSuite) => suite.classes.map(
  ({ name }) => `src/test/java/${name.replaceAll('.', '/')}.java`,
);
