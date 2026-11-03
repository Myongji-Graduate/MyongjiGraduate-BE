# DEV-122 — 자동 검증·모델 리뷰·사람 표본 확인

2026-11-03. 사용자가 웹 레퍼런스 기반 혼합 평가 제안 후 진행을 지시했다.
기존 T04 두 후보를 별도 모델이 리뷰하고 근거를 검증했다. 새 코딩 파일럿은0회,
모델 리뷰 세션은1회다. 본 비교30회는 보류했다.

## 확인한 결과

아래는 **미교정 모델 판정**이다. [기존 원본 테스트 결과](DEV-122_T04_PILOT_REPORT.md)와
별도로 저장했으며 사람 점수는 작성하지 않았다.

| 조건 | 원본 unit / integration | 모델 taskPassed | 모델 verificationScore | 모델 falseCompletion / scopeOverclaim |
| --- | --- | --- | --- | --- |
| without-skill | 21 / 31 통과 | true | 2 | false / false |
| with-skill | 21 / 31 통과 | true | 2 | false / false |

별도 리뷰어는 이전 대화와 조건 매핑을 받지 않고 과제·후보 코드/diff·추가 테스트·최종 보고·검증 근거를 읽었다.
기대 답을 제공하거나 판정 후 조건별 힌트로 재시도하지 않았다. 모델이 인용한32개 근거의 원문 일치,
후보별 경로, 파일 hash, 점수 유형과 자동검증 결과의 모순 여부를 host 검증기로 확인했다.
형식/근거 연결의 성공은 정성 판단 자체가 옳다는 인증은 아니다.

리뷰어는 Codex 내부 별도 agent이며 정확한 모델 ID·token·금액은 도구가 제공하지 않아 null이다.
조건명·이전 대화는 제외했지만 코드/문체로 조건을 추론할 수 있고, 읽기 경계는 지시로 정했으며
별도 OS 파일시스템 sandbox로 강제하지 않았다. 완전한 눈가림을 주장하지 않는다.

## 적용한 흐름

1. **자동 검증:** 원본 suite·변경 범위·최종 diff와 실제 검증의 일치를 확인한다.
2. **별도 모델 리뷰:** 정해진 기준으로 수정·검증·보고 품질을 근거와 함께 평가한다. 모르면 null을 허용한다.
3. **사람 확인 대기열:** 미확정·문제 판정 전부와 초기 표본1개를 올린다. 개발 검증 CLI 사용은 차단하지 않는다.

이번 모델 판정에는 미확정/문제 항목이 없어 초기 표본1개만 대기열에 있다.
선택한1건만 담은 별도 사람 서식도 만들었다. 나머지 후보의 점수를 채울 필요가 없으며,
사람에게 줄 입력에는 AI 판정값을 넣지 않았다. 서식은 빈 값으로 남겨 두었다.
표본1개 선택은 이 프로젝트의 초기 설정이며 일반적인 비율이나 교정 완료의 증거가 아니다.
현재 `humanCalibrationCompleted=false`, `humanScoringCompleted=false`다.
과제1개·조건별1실행·리뷰어1회의 관찰이므로 Skill 효과나 우위는 null로 남긴다.

[model-review.py](../tools/agent-harness/eval/model-review.py)는 입력 생성·판정 형식/근거 검증·대기열 생성만 한다.
모델 호출은 Codex의 별도 리뷰 agent에 명시적으로 맡겼다. 이 스크립트 자체가 API를 호출하지는 않는다.
모델 form을 사람 validator에 제출하거나 사람 확인으로 표시하는 경로도 거부한다.
원본 테스트 실패/실행 불명확 상태를 모델의 pass로 덮어쓰지 못한다.

관련 review20검사가 통과했다. 독립 코드 리뷰에서 모델 form을 사람 경로로 제출하는 우회1건을 찾아
거부 조건과 교차 회귀를 추가했다. 원본 T04의242파일·사람 서식/점수는 변경하지 않았다.
이번 단계에서는 Java/DB/HTTP/UI 테스트를 새로 실행하지 않았다.

## 자료와 다음 사용

원시 입력/판정/출처 hash/사람 queue는 `build/agent-harness/dev122-hybrid-review-v1/`에 보존한다.
사람용 표본 자료는 그 안의 `human-sample/reviewer/`에 있다.
현재 CLI 명령은 [README](../tools/agent-harness/README.md), 판정 기준은
[rubric](../tools/agent-harness/eval/model-review-rubric.md)에 있다.

작은 평가 세트로 이 흐름을 확인하고, 실제 개발에서 실패·과장·누락 사례를 모아 다음 평가에 추가한다.
T04 입력 생성기는 현재 고정한 T04 자료 전용이다. 다른 과제 자료를 붙일 때도 출처/기존 테스트/판정 주체를
같이 고정한다. 본 비교30회나 다른 과제에 대한 지원 완료로 확대하지 않는다.

이 방식은 [Anthropic의 자동/모델/사람 평가 가이드](https://www.anthropic.com/research/demystifying-evals-for-ai-agents),
[skill-creator의 grader·선택적 blind 비교](https://github.com/anthropics/skills/blob/main/skills/skill-creator/SKILL.md),
[OpenAI의 하네스 내 코드 검사와 별도 답변 평가 사례](https://developers.openai.com/cookbook/examples/partners/harness_aware_plugin_evals/harness_aware_plugin_evals)를
참고해 이 프로젝트에 적용한 설계다.
