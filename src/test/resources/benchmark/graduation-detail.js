import http from 'k6/http';
import { check, sleep } from 'k6';
import { SharedArray } from 'k6/data';
import { Trend, Rate } from 'k6/metrics';

const profiles = new SharedArray('synthetic students', () => JSON.parse(open(__ENV.PROFILES_FILE)));
const categories = [
  'COMMON_CULTURE',
  'CORE_CULTURE',
  'PRIMARY_MANDATORY_MAJOR',
  'PRIMARY_ELECTIVE_MAJOR',
  'PRIMARY_BASIC_ACADEMICAL_CULTURE',
];
const latency = new Trend('graduation_duration', true);
const validResponse = new Rate('graduation_response_valid');

export const options = {
  scenarios: {
    graduation: { executor: 'per-vu-iterations', vus: 100, iterations: 5, maxDuration: '2m' },
  },
  thresholds: {
    http_req_failed: ['rate==0'],
    graduation_response_valid: ['rate==1'],
    iterations: ['count==500'],
  },
  summaryTrendStats: ['avg', 'med', 'p(95)', 'p(99)', 'max'],
};

function canonical(value) {
  if (Array.isArray(value)) return value.map(canonical).sort();
  if (value !== null && typeof value === 'object') {
    return Object.keys(value).sort().map(key => [key, canonical(value[key])]);
  }
  return JSON.stringify(value);
}

export default function () {
  const profile = profiles[(__VU - 1) % profiles.length];
  const category = categories[(__VU - 1 + __ITER) % categories.length];
  const response = http.get(
    `${__ENV.BASE_URL}/api/v1/graduations/detail?graduationCategory=${category}`,
    { headers: { Authorization: `Bearer ${profile.token}` }, tags: { category } },
  );
  let valid = false;
  if (response.status === 200) {
    try {
      valid = JSON.stringify(canonical(response.json())) ===
        JSON.stringify(canonical(profile.expected[category]));
    } catch (_) {
      valid = false;
    }
  }
  latency.add(response.timings.duration, { category });
  validResponse.add(valid, { category });
  check(response, { '200 and matches uncached reference': () => valid });
  sleep(0.1);
}

export function handleSummary(data) {
  return { [__ENV.SUMMARY_FILE]: JSON.stringify(data, null, 2) };
}
