# DEV-122 P3 — mju-verify 비교 평가 명세

상태: **과제·채점·실행 조건 준비**, 모델 비교 미실행. `tasks.json`의 `frozenCommit`과
`agent` 설정은 아직 미확정이다. 현재 HEAD는 하네스가 커밋된 평가 기준선이 아니다.
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
  모델 snapshot이 노출되지 않으면 그 한계를 기록한다. 현재 구체적인 모델·reasoning 값은 미확정이다.
- 모델 호출 중 사람의 해결 힌트와 추가 에이전트는 사용하지 않는다. 사람 개입/승인 대기는 그대로 기록한다.
  예상 외 권한 질문은 사전 환경 준비 결함으로 기록하고 임의로 한 조건만 도와주지 않는다.
- 다운로드는 사전 준비한다. 실행 중 인터넷/원본 저장소 접근은 차단하고 필요한 합성 테스트 자원만 제공한다.
  Docker socket은 호스트 전체 접근을 줄 수 있으므로 개인 개발 호스트를 평가 에이전트에 그대로 노출하지 않는다.
  격리된 테스트 호스트 또는 제한된 실행 경계를 준비하지 못하면 integration 비교를 시작하지 않는다.
- timeout이면 진행 중인 자식 프로세스와 그 run의 자원을 정리하고 마지막 diff·로그를 보존한다.
  초기화나 채점 환경 문제는 `environment-error`, 에이전트의 잘못된 명령·실패한 수정은 실행 실패로 구분한다.
  모든 예정/시작 run을 장부에 남기며 재실행해도 이전 실패·timeout을 삭제하지 않는다.

## 5. 채점: 정답률, 검증, 보고를 분리

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

기능 판정은 테스트 결과를 우선한다. 보고/동등 검증/우회 해결 판단은 조건명을 가린 사람이
근거 인용과 함께 판정한다. 리뷰 자료에서 스킬명·조건 폴더·설정은 가리고 무작위 run ID를 사용한다.
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

채점 중 모델 judge는 쓰지 않는다. 조건별/과제별 결과 표, 실패·허위 완료 사례, 개입 수,
시간 분포를 보고한다. 30회도 5개 과제의 반복이므로 일반적인 개선율·통계적 우월성으로 확대하지 않는다.

## 6. 실행 전 통과해야 할 준비 조건

- [x] 5개 요청·단일 mutation·허용 파일·기존 oracle·예산 제안 정의
- [ ] 정상 기준선 커밋 및 모든 평가 입력의 파일 hash 고정
- [ ] 정확한 모델/reasoning/client/설정·도구 권한·최종 예산 고정
- [ ] 실제 평가 실행기의 스킬/홈/대화/Git/네트워크/Docker 격리와 로그 수집 검증
- [ ] 고정 기준선에서 정상 green → 각 mutation의 지정 assertion red 확인
- [ ] 외부 채점 경로에서 무수정 후보 red / 정상 복구 후보 green 및 최종 파일 hash 일치 확인
- [ ] T01 pilot 2회 및 외부 채점/보고 흐름 확인
- [ ] 본 실험 30회와 조건을 가린 채점 완료

준비 검사 및 CLI 사용 명령은 [CLI README](../tools/agent-harness/README.md)가 관리한다.
`eval:check`는 source anchor와 요청/테스트 연결을 검사할 뿐 모델 실행이나 효과 검증을 하지 않는다.
현재 작업 사본에서 수행한 mutation 점검은 아래 계획의 관찰 기록에 남기며, 커밋 고정 후의 재검증을 대체하지 않는다.

이 명세는 자동 에이전트 실행기를 구현하거나 유료 모델을 호출하지 않는다. 다음 작업은
기준선 고정과 격리된 pilot 실행 준비이며, 커밋·푸시는 별도 사용자 요청에 따라 진행한다.
