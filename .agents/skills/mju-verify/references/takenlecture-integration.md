# 이수과목 합성 DB·HTTP 검증

| 완료 조건 | Suite | 관찰 대상 |
| --- | --- | --- |
| transaction·revision·동시 쓰기·rollback·Redis 장애·catalog 변경 | `takenlecture-integration` | 기존 `TakenLectureCacheConsistencyIntegrationTest`의 실제 MySQL/Redis와 독립 application context |
| 인증된 API·캐시 OFF/SHADOW/ON 응답·과목 변경·읽기 시 학사 행 불변·장애 복구 | `takenlecture-http` | 기존 `TakenLectureCacheHttpIntegrationTest`의 새 production JAR와 loopback HTTP |

```sh
node tools/agent-harness/dist/cli.js doctor takenlecture-integration --json
node tools/agent-harness/dist/cli.js verify takenlecture-integration --offline
# API 경계가 변경됐거나 완료 조건에 포함될 때:
node tools/agent-harness/dist/cli.js doctor takenlecture-http --json
node tools/agent-harness/dist/cli.js verify takenlecture-http --offline
```

Java 21과 로컬 Docker가 필요하다. 필요한 이미지·설치·시간 제한·정리 계약은
[CLI README](../../../../tools/agent-harness/README.md)가 관리한다. doctor는 읽기 전용이며,
verify는 전용 합성 MySQL/Redis를 생성한다. 기존 DB·스냅샷을 연결하거나 opt-in 환경변수를
수동 조합해 다른 JAR·축소 benchmark를 실행하지 않는다.

HTTP는 긴 JUnit 시나리오 하나다. `junit.totals.tests: 1`과 상세 보고서의 HTTP 요청 수를
혼동하지 않는다. `report.json`의 실행 결과·fresh XML·cleanup을 보고, API 검증에서는
`artifacts.http`의 `report.json`도 읽어 `complete`, `checks`, 테스트 데이터 범위를 확인한다.
성능 측정 수치를 운영 개선율이나 Skill 효과로 보고하지 않는다.

실패·skip·timeout과 정리 확인 실패를 구분한다. 테스트를 통과시키려고 기대 응답이나
동시성 조건을 완화하지 않는다. 재현되지 않은 실패는 이전 보고서를 보존하고 기록한다.
어느 suite도 브라우저 UI나 모든 학과/입학년도의 학사 규칙을 검증하지 않는다.
