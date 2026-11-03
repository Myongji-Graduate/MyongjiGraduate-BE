# DEV-122 P3 — mju-verify 비교 평가 명세

상태: **T01 파일럿 2회와 외부 원본 unit 채점 완료**. 기준선은
`b579ff46f3571c487415a004434093cf15a7dd67`이며, [pilot-lock.json](../tools/agent-harness/eval/pilot-lock.json)에
실행 조건과 검사 결과를 기록했다. 사용자 전송 승인 후 실행했으며,
[파일럿 결과](DEV-122_SKILL_PILOT_REPORT.md)에 두 후보 통과와 실제 CLI 실행 경로 문제를 구분했다.
후속 진단에서 로그인 셸의 PATH 변경을 재현했고 비로그인 셸 설정의 모델 없는 unit 검증을 마쳤다.
[runtime-followup.json](../tools/agent-harness/eval/runtime-followup.json)은 새 설정을 별도 고정한다.
기존 pilot lock/결과를 덮어쓰지 않는다. 새 설정의 두 조건 재고정과 사전 검증은
[pilot-v2-lock.json](../tools/agent-harness/eval/pilot-v2-lock.json)에 완료했다. 실제 추가 모델 2회는
최초 승인 검토에서 차단된 뒤 명시적 추가 승인을 받아 완료했다. 양쪽 실제 CLI·외부 원본 unit
21통과와 남은 범위는 [v2 결과](DEV-122_SKILL_PILOT_V2_REPORT.md)에 기록했다.
이 문서와 `tools/agent-harness/eval/`은 평가자 전용이며 평가 에이전트에 제공하지 않는다.

## 1. 질문과 비교 대상

같은 제품 코드·테스트·CLI를 제공할 때, **mju-verify와 AGENTS의 스킬 안내를 추가하면**
작은 이수과목 회귀 수정의 정확성, 적절한 검증 선택, 완료 보고의 근거가 달라지는가?
스킬 내용만의 효과와 자동 발견·안내 효과를 분리하는 실험은 아니다. CLI 도입 자체의
효과, HTTP/UI 품질, 일반적인 개발 생산성도 이번 비교로 결론 내리지 않는다.

도구의 직접 테스트와 실제 에이전트 환경의 평가는 다른 증거다. 이 구분은
[OpenAI의 Harness-aware evaluation 안내](https://developers.openai.com/cookbook/examples/partners/harness_aware_plugin_evals/harness_aware_plugin_evals#overview)를 참고했다.
아래 과제·시간·채점 규칙은 이 프로젝트를 위한 자체 설계다.

## 2. 고정할 과제 5개

기계 판독 정의: [tasks.json](../tools/agent-harness/eval/tasks.json).
각 과제는 정상 기준선에 **한 가지 합성 회귀만** 적용한다. 과제끼리 누적하지 않는다.
정확한 mutation과 실패해야 할 기존 테스트는 평가자 정의에만 보관한다.

| ID | 사용자에게 주는 문제 | 외부 채점 최소 suite | 시간 상한 |
| --- | --- | --- | --- |
| T01 | 다른 사용자의 캐시 payload가 응답되는 문제 | unit | 15분 |
| T02 | SHADOW 모드가 DB 대신 Redis 값을 반환 | unit | 15분 |
| T03 | rollback한 읽기가 Redis/local cache에 남음 | unit | 15분 |
| T04 | 단일 과목 삭제 후 저장 이수 학점이 남음 | unit + integration | 20분 |
| T05 | 쓰기 transaction에서도 cache를 읽음 | unit + integration | 20분 |

T01~T03은 동일 캐시 클래스의 단위 계약이며 T04~T05는 DB/transaction 경계를 포함한다.
5개는 서로 독립 실행하는 과제이지, 도메인 전반을 대표하는 독립 표본은 아니다.
T04의 기존 삭제 단위 테스트는 포트 호출만 확인하므로 그 통과만으로 완료할 수 없다.
새 테스트 작성은 허용하지만 외부 채점은 고정한 원본 테스트와 제품 변경으로 수행한다.

공통 요청은 `prompts/common.md` 다음에 해당 `prompts/Txx.md`를 붙인 정확한 바이트다.
조건 이름·스킬 사용 지시·정답·채점 테스트 이름을 사용자 요청에 넣지 않는다.
허용 제품 파일은 과제별 `source` 한 개다. `src/test/java/`의 **새 파일 추가만** 허용하며,
기존 테스트·설정·의존성·하네스·AGENTS·스킬 변경은 허용하지 않는다.

## 3. 두 조건과 정답 유출 방지

| 항목 | without-skill | with-skill |
| --- | --- | --- |
| 회귀 코드, 기존 테스트, CLI, CLI README | 동일 | 동일 |
| 공통 개발/보안 지침, 요청, 도구 권한 | 동일 | 동일 |
| `.agents/skills/mju-verify/` | 없음 | 고정한 원본 3파일 |
| AGENTS 마지막 스킬 안내 문장 | 없음 | 아래 한 문장 |

추가 문장: `이수과목 서비스·캐시 변경 검증에는 mju-verify를 사용하고 관련 기능 지도를 읽는다.`

실제 비교 사본을 만드는 규칙:

1. 검토·커밋된 정상 기준선에서 필요한 tracked 코드·테스트·빌드 파일과 CLI만 export한다.
   `.git`, 원본 worktree, 개인 디렉터리, snapshot/state, `.env`, 비밀값, 과거 build/로그,
   평가 명세·task manifest·seed·채점 결과는 포함하지 않는다. export 파일 allowlist를 고정한다.
2. 두 조건의 공통 AGENTS는 원본의 `## DEV-116 Workflow` 직전까지를 사용한다.
   해당 절부터 문서 끝까지는 두 조건 모두 제외한다. 이후 with-skill에만 위 문장을 붙인다.
   보안·테스트·코드·커밋 규칙은 공통 부분에 그대로 남는다.
3. CLI README의 P3 준비 절과 package.json의 `scripts.eval:check`도 두 조건 모두 제외한다.
   스킬의 계획 문서 링크를 위해
   `docs/DEV-122_AGENT_HARNESS_PLAN.md`에는 두 조건 동일한 중립 stub만 둔다:
   `개발 명령과 검증 범위는 tools/agent-harness/README.md를 참고한다.`
   나머지 작업 계획/평가 문서와 관련 안내 링크는 export하지 않는다.
4. mutation을 적용한 뒤 **회귀가 들어 있는 단일 root commit**으로 사본을 초기화한다.
   정상 코드의 부모 commit·Git object·reflog·remote·alternates·worktree 연결을 남기지 않는다.
   `git show HEAD`도 회귀 코드여야 한다. 정답을 알고 있는 이 대화는 평가 실행에 재사용하지 않는다.
5. 실행마다 독립된 사용자 홈·세션·상위 경로·파일시스템 접근 범위를 사용한다. 다른 run,
   원본 checkout, 평가자 저장소, 개인 스킬/기억/대화/커넥터를 읽지 못하게 한다.
   **환경변수 하나를 바꾸거나 새 대화를 여는 것만으로 격리를 달성했다고 간주하지 않는다.**
   에이전트가 실제로 받은 instruction/skill inventory와 접근 경계 증거가 없으면 비교를 시작하지 않는다.
6. 읽기 가능한 모든 입력 파일의 경로·SHA-256, 최종 요청, AGENTS, 스킬 inventory,
   클라이언트/모델 설정을 기록한다. 의도한 스킬 3파일과 안내 문장 이외의 조건 차이는 오류다.
   알려진 개인/global 스킬이 추가로 노출되면 환경을 고친 후 새 run ID로 다시 시작한다.

고정된 CLI/테스트를 보고 해결할 수 있는 과제다. 숨겨진 정답을 추측하는 능력보다
수정·검증·근거 보고 흐름을 비교한다. 스킬 제공 여부와 실제 로드는 별도 필드다.
파일 읽기나 trace로 관찰되지 않은 로드는 `unknown`으로 남긴다.

## 4. 예산, 실행 순서, 중단 규칙

- 파일에 제안한 예산: unit 과제 900초, integration 과제 1,200초. 첫 모델 요청부터
  최종 응답 또는 강제 중단까지이며 도구 실행·재시도도 포함한다. 환경 준비와 외부 채점은 별도 측정한다.
- pilot: T01 × 2조건 × 1회 = **2회**, 합계 최대 30분. 동작·로그·격리·채점 연결 점검용이며
  본 실험 결과에 합치지 않는다. pilot 후 설계를 바꾸면 버전을 올리고 다시 고정한다.
- 본 실험: 5과제 × 2조건 × 3회 = **30회**, 에이전트 실행 합계 최대 510분.
  각 반복은 새 환경·새 대화로 실행한다. 동일 머신에서 한 번에 하나씩 실행해 자원 경쟁을 줄인다.
- 순서: `SHA256(orderSeed + ':' + taskId + ':' + repetition + ':' + condition)`을
  오름차순 정렬한다. repetition은 1부터 시작하며 seed는 `DEV-122-P3-v1`이다.
  정렬된 30개 목록을 실행 전 저장한다. 이 seed는 실행 순서용이며 모델 재현 seed가 아니다.
- 동일 모델 식별자, reasoning, client 버전, 설정, 도구, 네트워크·권한, 의존성/이미지/캐시 상태를 사용한다.
  모델 snapshot이 노출되지 않으면 그 한계를 기록한다. 파일럿은 `gpt-6-astra / high`,
  Codex CLI `0.166.0`으로 고정했다. 정확한 모델 snapshot은 노출되지 않아 null이다.
- 모델 호출 중 사람의 해결 힌트와 추가 에이전트는 사용하지 않는다. 사람 개입/승인 대기는 그대로 기록한다.
  예상 외 권한 질문은 사전 환경 준비 결함으로 기록하고 임의로 한 조건만 도와주지 않는다.
- 다운로드는 사전 준비한다. 실행 중 인터넷/원본 저장소 접근은 차단하고 필요한 합성 테스트 자원만 제공한다.
  Docker socket은 호스트 전체 접근을 줄 수 있으므로 개인 개발 호스트를 평가 에이전트에 그대로 노출하지 않는다.
  격리된 테스트 호스트 또는 제한된 실행 경계를 준비하지 못하면 integration 비교를 시작하지 않는다.
- timeout이면 진행 중인 자식 프로세스와 그 run의 자원을 정리하고 마지막 diff·로그를 보존한다.
  초기화나 채점 환경 문제는 `environment-error`, 에이전트의 잘못된 명령·실패한 수정은 실행 실패로 구분한다.
  모든 예정/시작 run을 장부에 남기며 재실행해도 이전 실패·timeout을 삭제하지 않는다.

## 5. 채점: 정답률, 검증, 보고를 분리

후속 제한 진단: [T04 파일럿](DEV-122_T04_PILOT_REPORT.md)은 추가 승인2회·각1200초로 별도 실행했다.
두 조건의 원본 unit21/integration31을 독립 채점했고, 본 비교30회의 표본에 합치지 않는다.
조건을 가린 사람 자료를 준비했으며 정성 지표와 Skill 효과는 아직 null이다.

평가자가 정상 기준선에서 만든 새 채점 사본에 **해당 과제의 mutation을 먼저 적용**한다.
그 사본에 후보의 회귀 root commit 대비 허용된 **제품 diff만** 적용한다. 정상 사본에
후보 diff를 바로 적용하면 무수정 후보도 통과할 수 있으므로 금지한다. 채점 전 허용 제품 파일의
최종 바이트 hash가 후보의 최종 파일과 같은지 확인한다. patch 적용 실패나 hash 불일치는
채점 인프라 오류로 남긴다. 원본 테스트·검증 CLI·빌드 설정을 사용하고 후보가 추가한 테스트는 별도 참고 자료로 보존한다.
고정 suite 전체의 fresh 결과를 검사하며 정확한 문자열 패치 일치는 요구하지 않는다.
캐시 비활성화·상수 반환·fixture 맞춤 분기는 검토해 정상 동작을 훼손하는 우회 해결로 판정한다.

| 지표 | 판정 규칙 |
| --- | --- |
| `taskPassed` | 허용 범위 준수 + 요구 suite 전부 통과 + 정상 기능을 끄는 우회 해결 없음. skip/timeout/누락은 통과 아님 |
| `verificationScore` 0 | 최종 제품 diff에 대한 성공 검증 증거 없음 |
| `verificationScore` 1 | 일부 fresh 검증은 있으나 과제의 필수 동작/경계를 확인하지 못함 |
| `verificationScore` 2 | 최종 diff에서 필수 suite 또는 같은 동작·경계를 확인하는 동등한 검증을 실제 실행하고 증거 제공 |
| `falseCompletion` | 최종 답변의 완료/해결 주장을 외부 채점이 반증하거나, 실행하지 않은 검증을 통과했다고 주장 |
| `scopeOverclaim` | 단위를 DB/HTTP/UI 통과로 표현하는 등 실제 실행 범위보다 넓은 검증 주장 |
| 비용·시간 | 모델/도구/채점 시간을 분리. 노출된 token·비용만 기록하고 미제공 값은 null. 추정값을 실제 비용으로 쓰지 않음 |

동등한 직접 Gradle 실행도 인정한다. CLI 사용 횟수, 스킬 이름 언급, 테스트 개수 자체에 점수를 주지 않는다.
실패를 정직하게 보고한 run은 `taskPassed: false`, `falseCompletion: false`일 수 있다.
검증을 실행하지 않았다고 명시한 수정은 검증 점수가 낮아도 그 이유만으로 허위 보고로 판정하지 않는다.
환경 문제로 외부 채점이 불가능하면 `taskPassed`/`falseCompletion`의 판단 불가 값은 null로 남긴다.
모든 예정 run을 포함한 성공 수와 환경 오류 수를 함께 보고하고, 유효 run만의 비율을 쓰면 분모를 명시한다.

기능 판정은 테스트 결과를 우선한다. 보고/동등 검증/우회 해결 판단은 조건명을 가린 별도 모델이
근거 인용과 함께 먼저 판정한다. 사람은 초기 교정 표본과 미확정·실패·판정 불일치 사례를 검토한다.
모델 점수와 사람 점수는 별도로 저장하며, 사람 검토 전 모델 점수를 사람 판정이나 교정 완료로 표시하지 않는다.
리뷰 자료에서 스킬명·조건 폴더·설정은 가리고 무작위 run ID를 사용한다.
최종 답변 내용으로 조건을 추론할 수 있는 경우 완전한 눈가림이라고 주장하지 않는다.
판정 기준의 모호함은 두 조건 모두에 동일하게 적용하고 변경 이력을 남긴다.

run별 필수 기록:

```text
runId, taskId, repetition, condition(채점 후 공개), orderIndex
baselineCommit, manifestSha256, inputFileHashes, promptSha256, instructionInventory
model, reasoning, clientVersion, configSha256, skillProvided, skillLoadObserved
startedAt, endedAt, status, elapsedSeconds, toolSeconds, gradingSeconds
finalProductDiffSha256, finalAnswerPath, tracePath, verificationArtifacts, externalGradingArtifacts
taskPassed, verificationScore, falseCompletion, scopeOverclaim, humanInterventions, reasons
usage(input/output/cached/reasoning tokens 또는 null), cost(관찰값 또는 null)
```

2026-11-03 사용자가 웹 레퍼런스 기반 혼합 평가 제안 후 진행을 지시해 모델 judge를 허용했다.
이전 사람 전용 서식/결과는 보존하며 [혼합 리뷰 결과](DEV-122_HYBRID_REVIEW_REPORT.md)를 별도 기록한다.
자동 검증 결과를 모델이 덮어쓰지 못하게 하고 근거 없는 판단은 null과 이유를 남긴다.
조건별/과제별 결과 표, 실패·허위 완료 사례, 개입 수,
시간 분포를 보고한다. 30회도 5개 과제의 반복이므로 일반적인 개선율·통계적 우월성으로 확대하지 않는다.

## 6. 실행 전 통과해야 할 준비 조건

- [x] 5개 요청·단일 mutation·허용 파일·기존 oracle·예산 제안 정의
- [x] T01 파일럿의 기준선 커밋·입력·의존성/도구 파일 hash 고정
- [x] 파일럿 모델/reasoning/client/설정·도구 권한·15분 예산 고정
- [x] 파일럿 스킬 inventory, 원본/다른 run/정답/개인 스킬/인증 파일/심볼릭 링크/인터넷 접근 차단 확인
- [x] T01 양쪽 제한 프로필에서 무수정 후보 red / 정상 복구 후보 green과 제품 파일 hash 기록
- [ ] 고정 기준선에서 나머지 4개 과제와 integration 실행 격리 확인
- [x] Linux VM/Java 21의 정상·T04/T05 회귀·복구 workload 8행 확인(모델 없는 사전 검증)
- [x] Linux Codex 명령·MCP/스킬 inventory·합성 인증·VM 간 카나리·timeout 점검(실제 OAuth 제외)
- [x] 승인된 Docker OAuth 등록·빈 VM의 지정 auth/provider 대체 값과 로컬 메타데이터 확인
- [x] OAuth provider 프로필 메타데이터·전용 kit의 로컬 HTTP 정책·사용량 GET1회 수용 확인
- [ ] ChatGPT provider 실행 프로필·허용 네트워크 경계·서버 인증/갱신 검증
- [x] 실제 에이전트 후보 diff를 외부 채점 사본에 옮기고 원본 unit 테스트로 검증
- [x] T01 pilot 2회 및 외부 테스트 채점/결과 보고 흐름 확인
- [x] 실제 에이전트 명령 경로에서 unit doctor/verify 정상 실행 확인(v2 두 조건)
- [x] T01 v2 사람 채점 패킷·빈 서식·근거 검증기 준비(실제 채점은 미완료)
- [ ] 나머지 과제와 본 비교 전체의 사람 채점 자료 연결
- [x] T04 원본 증거를 공통 채점 입력 검증기에 연결(점수는 null, source pin/파일 삭제/동일 제품 매핑 교체 거부)
- [x] 본 비교30회 순서·510분 상한 생성과 정적 hash 검증(승인·실행0, 전체 준비 미완료)
- [x] 기존 T04 두 후보의 별도 모델 리뷰·근거 검증·사람 표본 queue 확인(미교정 모델 판정, 신규 코딩 실행0)
- [ ] 본 실험 30회와 조건을 가린 채점 완료

준비 검사 및 CLI 사용 명령은 [CLI README](../tools/agent-harness/README.md)가 관리한다.
`eval:check`는 source anchor와 요청/테스트 연결을 검사할 뿐 모델 실행이나 효과 검증을 하지 않는다.
현재 작업 사본에서 수행한 mutation 점검은 아래 계획의 관찰 기록에 남기며, 커밋 고정 후의 재검증을 대체하지 않는다.

v2 자료 생성기는 후보 코드/CLI/외부 채점 diff 지문과 JUnit 수치를 대조한다. 조건 매핑은
리뷰어 폴더 밖에 보관하고, 검증기는 누락·다른 후보 인용·근거 변경을 거부한다. 값 null은 이유와
인용이 있어야 판단 불가로 기록한다. 검증기 통과는 정성 판단의 타당성이나 사람의 신원을 증명하지 않는다.
파일럿의 실제 정성 지표는 채점 전까지 null로 유지한다. 현재 생성기는 T01 v2에 한정한다.
DB/Redis 평가 런타임의 실제 준비 상태와 남은 조건은 [VM 준비 문서](DEV-122_INTEGRATION_EVALUATION.md)에 있다.
VM의 실제 8행 결과는 [사전 검증 보고서](DEV-122_INTEGRATION_PREFLIGHT_REPORT.md)에 있다.
이 VM의 정상 이력과 결과를 실제 평가 에이전트에 노출하지 않으며 실제 run에는 새 사본이 필요하다.
[Codex 경계 보고서](DEV-122_CODEX_VM_BOUNDARY_REPORT.md)는 모델 없는 명령·합성 인증과
승인된 실제 OAuth 등록·초기 대체 값 점검을 구분한다. 로컬 account=null은 서버 인증의 증거가 아니다.
[provider 후속 점검](DEV-122_PROVIDER_NETWORK_REPORT.md)에서 새 프로필·로컬 HTTP 정책과 사용량 GET만 확인했다.
SSH 중계·부정 HTTPS 차단 주체·실제 workload/모델 경로·갱신, 새 run 고정과 모델 추론 검증은 남아 있다.

## 7. 2026-11-02 파일럿 환경의 관찰값

- 기준선 커밋은 사용자 진행 요청에 따라 생성했다. 두 사본은 정상 이력이 없는 회귀 root
  commit 하나씩이며 remote/alternates가 없다. 입력 파일은 642/645개이고 의도한 4경로만 다르다.
- 전역 CLI `0.132.0`은 현재 설정의 `ultra`와 최신 파일 권한 구문을 지원하지 않았다.
  전역 설치·개인 설정은 유지하고 별도 임시 경로에 공식 npm 패키지 `0.166.0`을 고정 설치했다.
- 스킬 inventory API로 자동 설치된 시스템 스킬도 제외했다. 최종 스킬은 미제공 조건 0개,
  제공 조건 mju-verify 1개다. 개인 설정·execpolicy·Git/npm 설정의 상속을 배제한다.
- 기본 최소 런타임의 임시 경로 읽기는 다른 사본까지 허용할 수 있어 `/private/tmp`와
  `/private/var/folders`를 차단한 뒤 run별 작업·캐시·임시 경로만 다시 허용했다.
  실제 직접 읽기와 symlink 읽기를 검사했으며, 에이전트 명령의 인증 파일 읽기도 차단됐다.
- 로컬 Gradle/테스트 통신을 위해 loopback을 허용한다. 인터넷은 proxy 정책과 실제 소켓 검사로
  차단했다. 호스트 loopback 전체를 적대적 코드로부터 격리한 환경은 아니므로, 운영 서비스나
  Docker 접근이 필요한 integration 비교의 격리 증거로 재사용하지 않는다.
- 제한된 JVM에서 Gradle의 IPv6 bind와 Mockito self-attach가 실패했다. 두 조건 모두
  IPv4, run별 `java.io.tmpdir`, 기존 의존성인 Byte Buddy `1.14.19`의 시작 시 agent 로드를
  적용했다. 테스트와 mock 검증을 바꾸지 않고 양쪽에서 1실패/21 → 21통과를 확인했다.
- 사전 실행 뒤 두 Gradle home을 같은 dependency-only 사본으로 초기화했다. 3,709개 파일의
  경로/내용 hash가 일치하며 CLI dist와 node_modules 266개 파일도 일치한다. 최종 npm 타입 검사,
  실효 설정·스킬/접근 audit, 독립 리뷰가 완료됐다. 증거는 pilot-lock의 디렉터리에 있다.
- `tasks.json.agent.configSha256`은 `${CASE_ROOT}`로 경로를 정규화한 설정 template hash다.
  실제 경로가 들어간 두 설정 hash, 파일별 hash, 실행 환경 값은 preparation.json에 따로 보관한다.
- 스킬 제공 → 미제공 순서는 `SHA256('DEV-122-P3-v1:pilot:T01:1:' + condition)` 정렬로 고정했다.
  별도 supervisor로 15분 상한·JSONL·최종 답변·diff를 보존하며 승인된 에이전트 실행 2회를 완료했다.
- 자동 승인 검토는 저장소 소스·지침을 기존 ChatGPT 로그인으로 OpenAI 모델에 전송하는 명시적
  승인이 확인되지 않았다는 이유로 최초 실행을 거절했다. 이후 사용자의 명시적 승인으로 2회 실행했다.
  전체 자동 비교·독립 채점 실행기는 아직 제품화하지 않았다. 프론트·푸시·배포는 이번 범위가 아니다.
- 파일럿의 두 후보 모두 원본 unit 21개를 통과했고 직접 Gradle의 25/27통과 주장도 확인했다.
  두 조건에서 CLI 실패가 관찰됐으므로 단독 sandbox 검사만으로 실제 agent 경로가 준비됐다고
  판단하지 않는다. 정성 보고 판정은 독립 에이전트의 예비 리뷰이며 본 비교의 사람 채점은 별도다.
  한 과제·조건별 1회 결과를 Skill 개선 효과로 확대하지 않는다.
