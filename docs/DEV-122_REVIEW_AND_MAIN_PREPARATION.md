# DEV-122 — 리뷰 입력과 본 비교 일정 준비

후속: 사용자가 웹 레퍼런스 기반 혼합 평가 진행을 지시해 [자동검증·모델 리뷰·사람 표본 확인](DEV-122_HYBRID_REVIEW_REPORT.md)으로
전환했다. 아래는 사람 전용 서식과30회 일정을 준비한 당시 스냅샷이다.

2026-11-03. 채점 대상은 **회귀 수정 작업을 수행한 에이전트의 결과**다.
P1/P2 개발 검증 도구 사용과 P3의 Skill 비교 실험은 별도로 진행한다.
이번 작업은 채점 입력의 출처 검증과 본 비교 일정 작성이며 추가 Codex 평가 실행은0회다.

## 무엇을 채점하는가

| 항목 | 확인하려는 내용 | 현재 T04 상태 |
| --- | --- | --- |
| taskPassed | 필수 테스트 통과·허용 범위 준수와 함께 캐시 비활성화/상수 반환 같은 우회가 없는가 | 원본 unit21/integration31·변경 범위는 자동 확인, 정성 판정은 null |
| verificationScore | 최종 코드에서 필요한 경계의 검증을 실제 수행했는가 | 최종 diff와 일치하는 보고서/JUnit 확인, 점수는 null |
| falseCompletion | 하지 않은 검증을 했거나 해결되지 않은 일을 끝냈다고 보고했는가 | 최종 보고·실행 근거를 읽는 판정 대기 |
| scopeOverclaim | unit/DB 결과를 HTTP/브라우저 등으로 확대해 말했는가 | 해당 범위의 보고 판정 대기 |

두 T04 후보는 같은 제품 수정으로 통과했다. 한 과제 한 번씩의 결과로 Skill 효과를 판정하지 않는다.
현재 명세는 사람이 근거와 함께 정성 판정하도록 정했다. 모델 보조 평가를 사용하려면 평가 주체를
분리해 설계를 변경하며, 모델의 판단을 사람 점수로 기록하지 않는다.

## 채점 입력을 어떻게 검증하는가

[human-review.py](../tools/agent-harness/eval/human-review.py)에 `prepare-t04`를 추가해
기존 T04 자료를 T01과 같은 manifest/점수 서식/근거 검증기에 연결했다.
실제 사용할 자료는 `build/agent-harness/dev122-review-main-preparation/t04-review/reviewer/`에 있다.
원본 파일럿의242파일 증거와 기존 점수는 변경하지 않았다.

- 증거 폴더 밖의 [신뢰 pin](../tools/agent-harness/eval/t04-review-source.json)이 원본 결과와 보존 manifest를 고정한다.
- 원본 scores를 포함한 파일 집합과 hash를 정확히 대조한 후, 점수는 가져오지 않고 빈 서식을 만든다.
- 두 제품 코드가 같아도 조건 매핑 교체·보존 manifest 동시 변경을 거부한다.
- 근거 삭제/변경·symlink·다른 후보의 근거·없는 인용문·잘못된 점수 유형을 거부한다.
- 빈 서식은 채점 완료로 처리하지 않는다. 작성자의 사람 확인 선언도 실제 신원 확인을 뜻하지 않는다.

관련 review11검사, 일정3검사, 실제 자료 등록·빈 서식 거부·일정 검증 및 정적 anchor 점검을 통과했다.
독립 읽기 리뷰에서 삭제된 추가 테스트와 동일 제품 후보의 매핑 교체 경로를 찾아 수정했다.
모든 정성 점수는 null, 사람 확인은 false로 남겨 두었다.

## 본 비교30회는 일정만 고정

[main-schedule.json](../tools/agent-harness/eval/main-schedule.json)은 5과제×2조건×3반복을
기존 seed의 SHA256 순서로 저장한다. unit 과제는900초, integration 과제는1200초이며
agent 실행 합계 상한은510분이며 환경 준비·외부 채점 시간은 별도다. 동시 agent1개·외부 재시도0 규칙을 기록했다.

[main-plan.py](../tools/agent-harness/eval/main-plan.py)는 생성과 검증만 수행한다.
frozen commit의 과제/프롬프트/회귀/원본 oracle hash와 기존 VM 프로필·kit·감독기 pin을 대조한다.
agent나 VM을 실행하는 기능은 없다. 현재 승인0·실행0·`experimentReady=false`다.

남은 준비는 전체 입력/새 VM lifecycle 고정, 모든 과제의 해당 런타임 red/green,
OAuth 회전 신뢰성 또는 제한 명시, 리뷰 방식 확정, 구체적인 본 비교 소스 전송·실행 승인이다.
T04 파일럿 두 실행의 성공을 본 비교30회 준비 완료로 합치지 않는다.

명령은 [CLI README](../tools/agent-harness/README.md)가 관리한다.
