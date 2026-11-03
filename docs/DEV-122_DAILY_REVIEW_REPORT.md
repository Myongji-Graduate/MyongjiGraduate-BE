# DEV-122 — 백엔드 일상 변경 리뷰 연결

2026-11-03. T04 전용 평가 입력과 별개로, 실제 개발 변경을 독립 리뷰에 전달하는
[change-review.py](../tools/agent-harness/review/change-review.py)를 추가했다.
스킬의 [일상 변경 리뷰](../.agents/skills/mju-verify/references/change-review.md)와 CLI 가이드를 연결했다.
새 코딩 실험·30회 비교·DB/앱 실행은 하지 않았다.

## 사용할 수 있는 범위

- 명시적으로 선택한 백엔드 Java 코드/테스트, 하네스 코드, 스킬/개발 문서, 지정 CI 파일의 base/current와 diff.
- tracked·새 파일·삭제 파일. 선택한 파일/HEAD가 바뀌면 과거 리뷰를 stale로 거부한다.
- 현재 checkout의 CLI verify report와 JUnit counter 대조. 다른 코드/checkout의 report는 거부한다.
- 다른 검사는 실행 전후 선택 fingerprint와 로그 hash를 담은 caller receipt로 연결한다.
- 별도 모델 리뷰의 실제 source line·인용·결함/미검증 범위 검사. 점수나 사람 확인으로 승격하지 않는다.

리뷰어는 선택 파일 snapshot만 받으며 원본 workspace를 탐색하거나 코드/명령을 실행하지 않도록 지시한다.
이것은 OS sandbox가 아니다. 도구 자체는 모델을 호출하지 않고, 테스트를 실행하거나 소스를 수정하지 않는다.
출력은 새 디렉터리로만 만들고 선택 입력과 겹치는 위치는 거부한다.

설정/데이터/ignored·snapshot/state·symlink·경로 패턴은 코드 입력에서 제외한다.
선택한 내용 자체를 비밀값 검사/마스킹하는 도구는 아니므로 공유할 코드와 근거만 명시한다.
원본 source는 파일당256KiB, 생성 diff/검증 근거는 전체 내용2MiB budget 안에서 처리한다.
사용 명령과 receipt 형식은 [CLI README](../tools/agent-harness/README.md#일상-변경을-독립-리뷰에-전달)가 관리한다.

## 실제 사용으로 확인한 결과

이 도구·테스트·스킬2파일·CI를 포함한 선택5파일로 직접 리뷰 묶음을 만들었다.
선택 snapshot의 검사 전후 fingerprint가 같은 실제 unit 실행 로그를 caller receipt로 붙였다.

| 확인 | 관찰 |
| --- | --- |
| 자체 unit | 12개 통과. 새/삭제/ignored/비밀 경로·symlink·stale·위조 인용/JUnit·큰 diff/로그·출력 겹침 포함 |
| 독립 모델 리뷰 | 같은 리뷰어의3패스. 첫 패스에서 P2 1건, 수정 후 두 패스에서 새 지적 없음 |
| 발견/수정 | source 한도256KiB를 생성 diff에도 적용해 큰 정상 묶음을 후속 검사에서 거부하던 결함 |
| 추가 경계 | 선택한 삭제 파일을 출력 디렉터리로 재생성하지 않도록 겹침을 거부 |
| stale 경로 | 코드 수정 뒤 최초 리뷰의 상태 검사가 실제로 실패함을 확인하고 새 묶음/receipt/리뷰로 갱신 |
| 스킬 | bundled quick validator 통과. 시스템 Python에 없는 PyYAML은 기존 uv cache를 읽어 사용, 전역 설치 없음 |
| CI | 새 review unit step 추가·YAML parse 통과. 원격 GitHub 실행은 미실행 |

최종 입력과 판정은 `build/agent-harness/dev122-daily-review/bundle-final/`, 최초 지적과 중간 수정은
같은 증거 폴더의 앞선 bundle에 보존한다. 읽기 context는 처음 분리했고 이후는 정상적인 수정 재리뷰다.
리뷰어가 검사를 독립 실행한 것은 아니며, 판정값의 타당성을 인용 검사만으로 인증하지 않는다.
최종 상태는 [기계 판독 결과](../tools/agent-harness/review/daily-review-result.json)에 기록했다.
기존 T04 증거 242개는 보존 목록의 hash와 모두 일치한다.

## 적용 절차와 한계

변경 → 관련 검증 → 선택 snapshot/근거 생성 → 별도 리뷰 → 현재 상태/인용 대조 →
필요한 수정·재검증 → 완료/미검증 범위 보고 순서로 사용한다. 단순 문구 변경에는 별도 모델을 일괄 강제하지 않는다.
모든 변경에 사람 점수표나 Skill 비교 실험을 요구하지 않는다.

이 리뷰는 선택5파일 범위다. 전체 저장소·선택 밖의 의존성을 리뷰했다고 표시하지 않는다.
선택 밖의 문서 갱신으로 전역 tracked 지문은 달라졌지만, 최종 선택 snapshot은 현재와 일치한다.
CLI report의 tracked 지문은 untracked 내용을 보장하지 않고, caller receipt는 독립 실행 인증이 아니다.
이번 단계에 Java/DB/HTTP/UI 검증을 새로 수행한 것은 아니다.
현재 스킬 안내는 기존 T04 실험의 고정 버전 이후에 변경됐으므로 이 새 안내의 효과는 아직 측정하지 않았다.
실제 서비스 변경에 적용한 사용자 피드백과 원격 CI 확인은 다음에 추가한다.
