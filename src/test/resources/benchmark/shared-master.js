import http from 'k6/http';
import execution from 'k6/execution';
import { check } from 'k6';
import { SharedArray } from 'k6/data';
import { Rate, Trend } from 'k6/metrics';

const profiles = new SharedArray('synthetic profiles', () => JSON.parse(open(__ENV.PROFILES_FILE)));
const categories = ['COMMON_CULTURE', 'CORE_CULTURE', 'PRIMARY_MANDATORY_MAJOR',
  'PRIMARY_ELECTIVE_MAJOR', 'PRIMARY_BASIC_ACADEMICAL_CULTURE'];
const steady = __ENV.WORKLOAD === 'steady';
const duration = Number(__ENV.DURATION_SECONDS || 20);
const rate = Number(__ENV.RATE || 200);
const iterations = Number(__ENV.ITERATIONS || 800);
const expected = steady ? duration * rate : iterations;
const valid = new Rate('response_valid');
const latency = new Trend('graduation_duration', true);

export const options = {
  scenarios: {
    graduation: steady
      ? { executor: 'constant-arrival-rate', rate, timeUnit: '1s', duration: `${duration}s`,
        preAllocatedVUs: 100, maxVUs: 100, gracefulStop: '30s' }
      : { executor: 'shared-iterations', vus: 100, iterations, maxDuration: '2m' },
  },
  thresholds: {
    http_req_failed: ['rate==0'],
    response_valid: ['rate==1'],
    http_reqs: [`count==${expected}`],
    iterations: steady ? [`count>=${expected}`, `count<=${expected + 1}`] : [`count==${expected}`],
    ...(steady ? { dropped_iterations: ['count==0'] } : {}),
  },
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

function canonical(value) {
  if (Array.isArray(value)) return `[${value.map(canonical).sort().join(',')}]`;
  if (value !== null && typeof value === 'object') {
    return `{${Object.keys(value).sort().map(key => `${JSON.stringify(key)}:${canonical(value[key])}`).join(',')}}`;
  }
  return JSON.stringify(value);
}

export default function () {
  const index = execution.scenario.iterationInTest;
  // An arrival-rate executor can schedule one extra iteration at the duration boundary.
  if (index >= expected) return;
  const profile = profiles[index % profiles.length];
  const category = categories[Math.floor(index / profiles.length) % categories.length];
  const response = http.get(`${__ENV.BASE_URL}/api/v1/graduations/detail?graduationCategory=${category}`, {
    headers: { Authorization: `Bearer ${profile.token}` }, tags: { category },
  });
  let matches = false;
  if (response.status === 200) {
    try { matches = canonical(response.json()) === canonical(profile.expected[category]); }
    catch (_) { matches = false; }
  }
  latency.add(response.timings.duration, { category });
  valid.add(matches, { category });
  check(response, { '200 and matches uncached reference': () => matches });
}

export function handleSummary(data) {
  return { [__ENV.SUMMARY_FILE]: JSON.stringify(data, null, 2) };
}
