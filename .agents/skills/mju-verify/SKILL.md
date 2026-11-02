---
name: mju-verify
description: Verify MyongjiGraduate-BE takenlecture service and cache changes with scoped unit, synthetic DB/Redis, or authenticated HTTP suites. Use for 이수과목 변경 검증 or 캐시 회귀 검증 and report fresh evidence and coverage limits. Does not evaluate browser behavior or Skill effectiveness.
---

# 졸부 변경 검증

작업의 완료 조건과 변경 경로를 먼저 대조하고 관련 지도만 읽는다.

- 포트 호출·캐시 단위 계약: [단위 검증 지도](references/takenlecture-unit.md)
- commit·revision·동시성·실제 Redis 또는 인증 API 응답: [합성 DB/HTTP 지도](references/takenlecture-integration.md)

다른 기능이면 적절한 기존 테스트를 조사하고 현재 CLI가 그 기능을 검증한다고 주장하지 않는다.

## 도구 준비

저장소 루트에서 실행한다. 별도 worktree에도 동일한 명령을 사용한다.

```sh
npm ci --prefix tools/agent-harness --ignore-scripts
npm run build --prefix tools/agent-harness
node tools/agent-harness/dist/cli.js doctor --json
node tools/agent-harness/dist/cli.js list --json
```

이미 해당 코드로 빌드했고 의존성이 변하지 않았다면 설치와 재빌드를 반복할 필요는 없다.
실행 파일이 없는 첫 checkout에서는 위 준비가 필요하다. 설치는 npm registry 접근,
Gradle 최초 실행은 wrapper/의존성 다운로드가 필요할 수 있다.

## 범위에 맞게 실행

```sh
node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline
```

`--offline`은 이미 준비된 Gradle 캐시를 사용한다. 캐시가 부족하면 그 사실을 보고하고,
의존성 다운로드가 작업 범위에 포함될 때만 `--offline` 없이 재실행한다.
Java 21이 필요하다. `doctor` 실패는 개발 환경 문제이며 제품 결함의 증거가 아니다.

위 명령은 네 개의 unit 클래스를 실행한다. DB/HTTP가 완료 조건이면 관련 지도에 따라
명시적으로 해당 suite를 선택한다. 와일드카드로 전체 takenlecture 테스트를 대신 실행하지 않는다.

## 결과 읽기

CLI가 출력한 이번 실행의 보고서와 JUnit 결과를 확인한다. 종료 코드, 실제 테스트 수,
실패·오류·skip, 대상 코드와 검증 수준을 함께 확인한다. 실패한 명령을 통과로 요약하거나
이전 실행 XML을 새 코드의 증거로 재사용하지 않는다.

보고할 내용:

- 실행한 suite와 명령, 실제 결과와 증거 경로
- 검증한 동작과 작업의 완료 조건 사이에 남은 차이
- 환경 실패·미실행·skip 등 확인하지 못한 항목

단위·DB 통합·HTTP·UI·Skill 효과를 별도로 보고한다. 단위 테스트는 실제 DB를,
HTTP는 브라우저 새로고침을 증명하지 않는다. 합성 데이터 통과는 전체 학사 규칙의
정확성 증거가 아니다. 완료 조건에 필요한 추가 검증을 수행하거나 미검증으로 남긴다.
스킬을 사용했다는 사실만으로 Skill 효과가 측정된 것도 아니다.

실행 산출물은 `build/agent-harness/` 아래에 있으며 Git에서 제외된다.
추적 파일의 변경 지문은 untracked/ignored 파일 전체를 포함하지 않는다.
상세 계약과 개발 명령은 [CLI 가이드](../../../tools/agent-harness/README.md),
범위와 후속 단계는 [DEV-122 계획](../../../docs/DEV-122_AGENT_HARNESS_PLAN.md)을 확인한다.
