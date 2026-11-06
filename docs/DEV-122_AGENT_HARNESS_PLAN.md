# DEV-122 개발 검증 하네스와 Skill 평가

## 현재 상태

- 기준일: 2026-11-06
- 백엔드 브랜치: `chore/DEV-122`
- 프론트 브랜치: `chore/agent-harness` (별도 프론트 저장소)
- 기반: 최신 조회한 `origin/develop`, `861228e42f779afa8a517fd3ed05a6bfafc46906`
- 상태: [PR #416](https://github.com/Myongji-Graduate/MyongjiGraduate-BE/pull/416)에 게시했고 최초 head의 원격 CI가 통과했다. 추가 핵심 구현 독립 리뷰에서 발견한4건과 수정·회귀 검사는 [PR 리뷰 기록](DEV-122_PR_REVIEW_REPORT.md)에서 관리한다. 평가 보호 검사는51개, 일상 리뷰 도구 검사는16개로 늘었다. 원격 CI의 현재 head 결과는 PR checks에서 확인한다. 이전 [서비스 검증 적용 기록](DEV-122_SERVICE_VERIFICATION_REPORT.md)과 T01/T04 고정 증거는 유지한다.
- 후속: SonarCloud의 java:S5778 경고에 따라 예외 검증 람다 밖에서 userId를 준비한다. 수정 후 실행 근거와 Sonar 재분석 결과는 해당 PR의 최신 head/checks에서 확인한다.
- 다음 작업: 실제 서비스 변경에서의 사용자 피드백을 추가한다. 사람 교정·Skill 효과 일반화는 미완료이며 본 비교30회는 보류한다. 기존 코딩 실험 승인6회는 사용했고 추가 코딩 실험은 실행하지 않는다.
- 기존 `DEV-116` 체크아웃의 미커밋 변경·데이터·계획은 이 작업에 포함하지 않는다.

## 목적과 완료의 의미

사람과 코딩 에이전트가 같은 명령으로 검증 범위를 선택하고, 실제 실행 결과를
확인할 수 있는 개발 도구를 만든다. 졸부의 기존 테스트를 재사용하고 검증 누락과
근거 없는 완료 보고를 줄일 수 있는지 측정한다.

두 종류의 평가를 분리한다.

1. **제품 검증:** 변경한 서비스·API·사용자 경로가 명세를 만족하는가?
2. **Skill 평가:** 검증 스킬을 제공했을 때 에이전트의 개발 결과가 개선되는가?

P1의 단위 테스트 통과는 실제 Redis/MySQL 정합성, 프론트 새로고침,
전체 졸업 결과 또는 Skill 효과를 입증하지 않는다. 검증하지 않은 항목은
보고서에 남기고 단계 완료와 서비스 출시 가능 여부를 구분한다.

## 현재 기반과 첫 범위

| 확인한 자산 | 활용과 한계 |
| --- | --- |
| Java 21 / Gradle 8.10, JUnit / Mockito | 기존 빌드와 단위 테스트를 CLI에서 호출 |
| `TakenLectureCacheTest` | mock Redis와 직접 생성한 캐시의 단위 계약 |
| `DeleteTakenLectureServiceTest` | 단일·전체 삭제 포트 호출 |
| `GenerateTakenLectureServiceTest` | 맞춤 과목 저장 포트 호출; 실제 재계산 증거가 아님 |
| `FindTakenLectureServiceTest` | snapshot 조회와 이수 목록·학점 계산 |
| 기존 캐시 consistency / HTTP 통합 테스트 | 별도 opt-in 합성 Testcontainers; P2에서 재사용 검토 |
| 프론트 Jest / Cypress / MSW | 별도 프론트 브랜치에서 사용자 경로 검증으로 확장 |

첫 suite ID는 `takenlecture-unit`이다. 위 네 클래스는 Spring 애플리케이션이나
DB/Docker를 시작하지 않는다. `application-test.yml`에는 Testcontainers datasource가
있으므로 넓은 이름 패턴으로 테스트를 추가하지 않고 정확한 클래스 목록을 사용한다.

## 구성과 작업 흐름

- **메인 Codex:** 목표·범위·완료 조건을 정리하고 구현과 결과 통합을 맡는다.
- **`mju-verify` Skill:** 관련 기능 지도를 선택하고 CLI 실행·증거 해석 방법을 안내한다.
- **TypeScript CLI:** 환경 진단, 고정 suite 실행, fresh JUnit 결과 확인, JSON 보고서 작성.
- **독립 리뷰:** 의미 있는 변경의 재현·회귀·증거 누락을 확인한다. 파일 수정은 소유자를 나눈다.
- **기록:** 이 문서의 현재 상태와 단계별 실제 증거만 갱신한다. 원시 실행 로그는 build 아래에 둔다.

흐름: 요청 → 완료 조건 → 필요 자료·스킬 선택 → 구현 → CLI 검증 → 필요한 독립 리뷰
→ 수정·재검증 또는 완료 보고. 스킬은 절차이며 별도 상주 프로세스가 아니다.

## 단계별 계획

| 단계 | 산출물과 완료 조건 | 상태 |
| --- | --- | --- |
| P0 범위·기준 확정 | 기존 변경과 격리된 브랜치, 검증 대상·단계·비목표 기록 | 완료 |
| P1 단위 검증 도구 | doctor/list/verify CLI, mju-verify, 첫 기능 지도, 실제 suite 실행, 실패 경로 테스트 | 완료 |
| P2 실제 경로 검증 | 합성 DB/HTTP suite와 프론트 추가·삭제·새로고침 계약; 범위와 입력 한계 구분 | 완료(명시한 범위) |
| P3 Skill 비교 평가 | 고정 작업·실행 조건·독립 채점, baseline/skill 비교 보고서 | 작은 T01/T04 파일럿·T04 원본 oracle·모델 리뷰 확인. 사람 교정·일반화 판정 미완료, 30회 보류 |
| P4 사용성·배포 준비 | 재현 가능한 가이드, CI 결과, 실제 사용 피드백과 제한 정리 | 일상 리뷰·스킬 연결·서비스 단위 회귀 보강의 로컬 검사/리뷰 완료. 원격 CI는 PR checks에서 관리, 사용자 피드백 대기 |

### P1: 최소 CLI와 프로젝트 스킬

예정 파일:

- `tools/agent-harness/`: 별도 npm 패키지, strict TypeScript, CLI와 테스트, 사용자 가이드
- `.agents/skills/mju-verify/SKILL.md`: 이 저장소 검증 진입점
- `.agents/skills/mju-verify/references/takenlecture-unit.md`: 단위 검증 기능 지도
- `.github/workflows/agent-harness.yml`: 도구 자체 검사와 첫 suite의 CI 정의
- `AGENTS.md`: DEV-122 계획과 검증 진입점만 추가

CLI 계약:

```text
jolbu-dev doctor [suite] [--json]
jolbu-dev list [--json]
jolbu-dev verify <suite> [--offline] [--timeout-ms N]
```

`doctor`는 Git 루트, 도구·Java 버전과 필요한 파일을 확인하며 서비스나 DB를 시작하지
않는다. `verify`는 고정된 명령과 인자를 사용한다. 임의 shell 명령을 명세에서 받지 않는다.
의존성 최초 다운로드가 필요할 수 있으며 `--offline`은 설치된 캐시만 사용한다.

완료 조건:

- [x] Node 20 이상에서 빌드·타입 검사와 의미 있는 CLI 테스트 통과
- [x] doctor/list 정상·오류 경로 확인
- [x] 허용된 네 클래스의 테스트를 실제 실행하고 새 JUnit XML 확인
- [x] 실행 실패·timeout·누락 XML·전부 skip을 성공으로 표시하지 않음
- [x] 대상 코드 버전·변경 지문·검증 수준·명령·시간·상태·미검증 범위를 보고서에 기록
- [x] 과거 XML을 새 실행의 증거로 재사용하지 않음
- [x] 스킬 frontmatter·참조 링크 검사와 독립 사용 점검
- [x] GitHub CI 정의는 로컬 실행 결과와 구분; 실제 원격 실행 전에는 통과로 표시하지 않음

### P2: 합성 환경의 사용자 동작

기존 `TakenLectureCacheConsistencyIntegrationTest` 및
`TakenLectureCacheHttpIntegrationTest`를 먼저 조사한다. 실제 DB/Redis, HTTP, UI를
각기 다른 검증 수준으로 기록하고 단위 suite에 조용히 섞지 않는다.

현재 backend suite는 `takenlecture-unit`, `takenlecture-integration`, `takenlecture-http`다.
실행·이미지·정리 계약의 단일 안내는 `tools/agent-harness/README.md`가 관리한다.

프론트는 별도 브랜치에서 기존 Cypress를 재사용한다. 첫 경로는 과목 추가·삭제 후
목록과 졸업 결과 확인 및 새로고침 유지다. 성공/실패/빈 상태 중 변경에 관련된 경로를
선정한다. 사용하는 학생은 합성 fixture이며 기존 실학생 스냅샷은 사용하지 않는다.

완료 기준은 화면 캡처만이 아니라 사용자 동작, 응답, 기대 결과와 지속 상태 확인이다.
임시 프로세스·포트·볼륨은 실행 소유권으로 정리하고 증거 파일은 보존한다.

프론트 UI는 stateful 합성 API fixture와 실제 Next/Cypress를 사용한다. 기존 mock의
학점이 고정돼 있어 이 경로에는 재사용하지 않는다. backend HTTP와 frontend UI를
각각 통과해도 실제 Spring과 브라우저를 연결한 E2E 통과로 합치지 않는다.

### P3: Skill 자체의 비교 평가

과제·변경 범위·격리·채점·실행 순서는 [P3 평가 명세](DEV-122_SKILL_EVALUATION_SPEC.md),
기계 판독 과제는 [tasks.json](../tools/agent-harness/eval/tasks.json)이 관리한다.

- 합성 회귀 5개: 사용자 payload 구분, SHADOW 응답, rollback 발행, 삭제 학점, 쓰기 transaction 우회.
- CLI·제품·테스트는 공통이며 mju-verify와 AGENTS 안내의 제공 여부만 비교한다.
- pilot 2회 이후 본 비교 30회 제안. 과제별 15/20분, 본 비교 에이전트 시간 상한 510분.
- 정상 Git 이력·정답 정의·개인 스킬/대화 유출을 막고, 회귀 코드에 후보 diff를 적용해 외부 채점한다.
- 수정 성공, 검증 범위, 허위 완료, 시간·관찰 가능한 비용을 분리한다. 실패/timeout/환경 오류를 보존한다.
- T01 기준선은 `b579ff46`, 모델은 `gpt-6-astra / high`, CLI는 `0.166.0`으로 고정했다.
  [pilot-lock.json](../tools/agent-harness/eval/pilot-lock.json)이 파일럿 조건과 증거를 관리한다.
  새 T04 두 조건의 integration 격리와 제한 실행기를 준비했다. 전체 30회 자동 비교·채점 실행기는 아직 완료되지 않았다.

회귀 탐지 점검은 과제의 유효성 증거이며 Skill 효과가 아니다. 5개 과제의 반복 결과를
일반적인 생산성 개선율로 확대하지 않는다. 승인된 v1/v2 파일럿은 기존 ChatGPT 로그인으로
각 2회 실행했으며, 별도 API 키·실제 비용·개선 수치는 없다.

### P4: 사용 가능한 도구로 정리

README에 해결하려는 문제, 설치/빌드, 명령, 결과 해석, 실패 시 대응, 한계를 설명한다.
고정 의존성과 lockfile, strict 타입 검사, 실행 오류 계약, CI를 함께 제공한다.
졸부에서 사용한 뒤 공통 부분의 재사용 가치가 확인되면 별도 CLI 저장소 분리를 검토한다.
새 저장소 생성·공개·커밋·푸시·배포는 이 문서 작성만으로 실행하지 않는다.

## 범위와 결정

- 제품 Java 코드·학사 규칙은 P1에서 변경하지 않는다.
- TypeScript는 CLI의 명세·프로세스·보고서 계약에 사용한다. 기존 Java/Python 검증을 재작성하지 않는다.
- 각 실행은 자체 산출물 디렉터리를 사용한다. Git HEAD만으로 dirty 작업의 동일성을 주장하지 않는다.
- Git tracked 변경 지문을 남기더라도 untracked/ignored 자료 전체의 동일성 증거로 쓰지 않는다.
- 실행을 성공시키려고 기대 조건을 완화하거나 미실행 테스트를 통과로 기록하지 않는다.
- DEV-116 자료·기존 DB·실학생 데이터·키·유료 모델·배포는 이번 첫 구현에 필요하지 않다.
- 상시 다중 에이전트, 예약 실행, 자동 머지는 도입하지 않는다.

## 작업 증거

### 2026-11-02 — P0

- 원본 체크아웃은 `DEV-116`이며 기존 미커밋 변경이 있음을 확인했다.
- `git fetch origin develop` 후 `861228e4`를 기준으로 별도 managed worktree와
  `chore/DEV-122` 브랜치를 생성했다. 원본 체크아웃을 전환하거나 파일을 옮기지 않았다.
- 기존 네 단위 테스트의 직접 생성/Mockito 경로를 확인했다. 선언된 `@Test`는 21개이며
  실제 실행 통과 수는 P1에서 확인한다.
- 다음: CLI 구현·실행과 독립 검토, 관찰한 결과에 따라 P1 체크리스트 갱신.

### 2026-11-02 — 계획 독립 검토

- baseline에도 스킬 자동 발견과 AGENTS 안내가 노출되면 비교가 오염된다는 지적을 반영했다.
- P3에서 제품 코드·CLI는 동일하게 두고 스킬 제공 조건만 분리하며 지침 지문을 기록한다.
- 스킬 정적 validator 통과. 시스템 Python에는 PyYAML이 없어 기존 개발 Python 환경의
  validator를 사용했다. 정적 통과는 자동 발견·행동 개선의 증거가 아니다.

### 2026-11-02 — P1 구현과 실제 실행

- `tools/agent-harness`에 런타임 npm 의존성 없는 TypeScript CLI를 추가했다.
  root npm 설정·제품 Java 코드·기존 테스트·기존 CI는 변경하지 않았다.
- `npm ci --prefix tools/agent-harness --ignore-scripts --no-audit --no-fund` 실제 성공:
  고정 lockfile에서 개발 의존성 3개 설치. 새 CI YAML 구문도 검사했다.
- 첫 실제 `verify takenlecture-unit --offline`: 21통과, 실패/오류/skip 0,
  4개 fresh JUnit XML. 실행 ID는 `2026-11-02T01-09-25.339Z-596f365d-42fb-48d1-b5fa-505bffb20234`다.
- 독립 리뷰에서 launcher 종료 후 남는 자손 프로세스를 재현하고 그룹 KILL과 정리 대기를
  수정했다. doctor의 Java 선택도 wrapper의 JAVA_HOME 우선순위에 맞췄다.
  수정 관련 2개 회귀 검사는 독립 리뷰에서 통과했다.
- 새 에이전트가 스킬과 checkout만 받아 doctor/list/verify를 수행했다. 최종 CLI로
  21통과, 실패/오류/skip 0이며 전후 tracked 지문이 동일했다. 이 실행의 결과는
  `build/agent-harness/2026-11-02T01-14-17.036Z-9a307990-5772-4687-8954-c3e8df42a8a1/report.json`에 있다.
  에이전트는 단위 계약과 DB/HTTP/UI·Skill 효과 미검증을 구분해 보고했다.
- CLI 전체 검사에서 17통과/1실패가 한 번 발생했다. 종료 순간 테스트 fixture의
  heartbeat 파일이 truncate된 채 남는 경쟁 조건이었다. fixture를 atomic rename으로
  고치고 최초 heartbeat를 PID보다 먼저 기록했다. 해당 회귀를 3회 연속 통과한 뒤
  구현 담당의 전체 18검사가 통과했다. 제품 코드 변경이나 검증 조건 완화는 없었다.
- 최종 worktree에서 메인 에이전트가 `npm test --prefix tools/agent-harness`를 재실행해
  strict TypeScript 빌드와 18통과, 실패/skip 0을 확인했다. 스킬 validator,
  로컬 Markdown 링크, 추가 소스 공백 검사와 `git diff --check`도 통과했다.
- 관리형 worktree 파일·Gradle 캐시 접근은 sandbox에서 EPERM이 발생해 승인 검토를
  거친 동일 명령으로 실행했다. 이 환경 오류를 제품 테스트 실패로 집계하지 않았다.
- 실제 GitHub CI, Node 20 환경의 직접 실행, DB/Redis 통합, HTTP/UI, Skill 비교 실험은
  아직 실행하지 않았다. 로컬 런타임은 Node 24.1.0 / Java 21이다. 커밋·푸시·배포도 없다.

### 2026-11-02 — P2 구현과 검증 증거

- CLI의 선택 범위를 unit/integration/http로 분리했다. 선택한 opt-in, 새 JAR,
  fresh XML, 고유 container label, cleanup 확인을 보고서에 남긴다.
- 첫 통합 시도는 Docker Desktop Ryuk socket 설정 문제로 실패했다. 컨테이너 내부
  socket 경로를 분리한 뒤 31회가 통과했으나 반복 테스트 이름 중복으로 CLI가 거부했다.
  두 `@RepeatedTest`에 메서드명을 포함시켰다. 테스트 단언은 완화하지 않았다.
- 독립 리뷰가 Testcontainers의 전역 `tc.host` 우선순위로 다른 Docker를 고를 가능성을
  발견했다. Test JVM의 home을 실행별로 격리하고 실제 endpoint를 생성 전에 비교한다.
- 해당 수정 후 CLI 자체 23검사와 DB/Redis 통합 31회 통과, 실패/오류/skip 0.
  통합 실행 ID: `2026-11-02T01-42-57.359Z-1bedc00e-85fc-460b-a728-0eb8ea1c7efd`.
- 격리 보완 전 HTTP 완주: JUnit 1시나리오, HTTP 42,897요청, 405측정 묶음,
  3개 모드의 변경·동시성·Redis 장애 9검사 통과. 실행 ID:
  `2026-11-02T01-33-44.441Z-97c69f4f-f83b-424e-9754-8c0e39d489da`.
  해당 실행의 owned container 잔존 0.
- 최종 격리 코드 HTTP 재실행도 완주: 실행 ID
  `2026-11-02T01-43-34.505Z-b98b60f1-ceeb-4fd7-8baa-b6ea998010c5`.
  1 JUnit 시나리오, 42,897 HTTP 요청, 405측정, 9검사 통과 및 cleanup 확인.
  이어 unit 21개도 재검증 통과:
  `2026-11-02T01-49-13.154Z-b6607f7e-0da1-4298-b183-abc9a3a5e27f`.
- 프론트는 `origin/dev`의 `a76e7fc`를 기준으로 별도 worktree를 만들었다.
  원본 프론트 dirty 변경을 옮기지 않았고 lockfile 의존성 1,859개를 offline 설치했다.
- UI 초기 실패에서 Cypress head script 삽입과 스타일 link의 hydration 충돌을 확인했다.
  공식 Cypress #27204의 Suspense 방식으로 수정했으며 hydration 예외를 무시하지 않는다.
  이후 개발 모드 MSW가 설정과 무관하게 켜져 fixture 요청을 가로채는 원인을 수정했다.
  API 주소·합성 fixture와 실제 브라우저의 요청 경로를 확인했다.
- 프론트 실행기의 독립 리뷰에서 SIGTERM/timeout 정리와 정확한 spec 확인 누락을
  발견해 보완했다. 별도 browser worker 그룹을 관리하고 fixture mutation도 확인한다.
  실제 무한 대기 합성 자식을 SIGTERM으로 중단하는 회귀 검사 통과: Next와 browser
  모두 종료, cleanup 확인, 실패 보고서 보존.
- 프론트 최종 UI 1시나리오 통과: 로그인 → 과목 추가 → 목록/남은 학점 9→6 →
  새로고침 유지 → 삭제/남은 학점 6→9 → 새로고침 유지. POST/DELETE 각 1회,
  정확한 spec, 실패/pending/skip 0, 추적 코드 지문 불변 및 cleanup을 확인했다.
  최종 증거는 프론트 worktree의
  `build/agent-harness/2026-11-02T01-58-48.159Z-0311c821-3506-4abb-a6bd-209fbfa31228/report.json`이다.
- 추가 버튼은 visible/enabled 확인 후 native DOM click으로 활성화했다. Cypress 13/Vaul의
  합성 포인터는 submit이 없었고 Enter는 중복 POST가 발생했다. 이 호환성 문제를 해결한 것으로
  보고하지 않으며 실제 포인터/키보드 입력은 미검증이다. 스크린샷만으로 성공을 판정하지 않았다.
  실제 Spring+브라우저 E2E, production 프론트 빌드와 Skill 효과 역시 미검증이다.
- 프론트 TypeScript 전체 검사, 변경 파일 ESLint, 실행기 중단 회귀 통과.
  최종 독립 리뷰에서 기존 BE/FE 지적 해결을 확인했다. 스킬 validator·링크 검사와
  양 저장소 `git diff --check` 통과. 커밋·푸시·배포는 하지 않았다.
- 프론트 실행 안내: 별도 worktree
  `/Users/jinhyeon/.codex/worktrees/dev-122-ui/myongji-graduate-next/tools/agent-harness/README.md`.
  현재 브랜치는 BE `chore/DEV-122`, FE `chore/agent-harness`다. 프론트는 설명형
  브랜치 관례에 맞춰 최초 `chore/DEV-122`에서 이름만 변경했으며, DEV-122를 프론트
  GitHub 이슈 번호로 간주하지 않는다. 작업 파일·기준 커밋·원본 체크아웃의 변경은 보존했다.

### 2026-11-02 — P3 평가 준비와 과제 유효성 점검

- [평가 명세](DEV-122_SKILL_EVALUATION_SPEC.md), 5개 과제의 요청·mutation·oracle,
  읽기 전용 `eval:check`를 추가했다. 두 조건의 입력 차이, 정답/Git/개인 스킬 격리,
  성공·검증·허위 완료의 분리 채점, pilot 2회/본 비교 30회와 시간 예산을 정의했다.
- 독립 리뷰에서 정상 사본에 후보 diff를 적용하면 무수정 후보도 통과할 수 있는 문제를
  발견했다. 같은 회귀를 먼저 넣고 후보 diff를 적용한 뒤 최종 파일 hash를 비교하도록
  명세를 수정했다. 무수정 red/복구 green 채점 gate도 추가하고 리뷰에서 해소를 확인했다.
- 모델 없이 임시 detached worktree에서 정상 코드와 과제별 단일 mutation을 순차 실행했다.
  현재 변경 중인 하네스·통합 테스트를 명시적으로 복사한 준비 점검이며 고정 커밋 평가가 아니다.

| 준비 점검 | unit 결과 | integration 결과 |
| --- | --- | --- |
| 정상 사본 | 21통과 | 31통과 |
| T01 사용자 구분 누락 | 1실패/21, 지정 oracle 검출 | 미실행 |
| T02 SHADOW 응답 회귀 | 1실패/21, 지정 oracle 검출 | 미실행 |
| T03 commit 전 발행 | 3실패/21, 지정 oracle 2개 모두 검출 | 미실행 |
| T04 삭제 학점 갱신 누락 | 21통과 | 1실패/31, 지정 oracle 검출 |
| T05 쓰기 transaction 허용 | 21통과 | 1실패/31, 지정 oracle 검출 |

- 위 9회 모두 error/skip 0이며, 회귀 run의 실패는 의도한 assertion 실패다.
  통합 3회는 소유 컨테이너 cleanup 확인, 모든 run은 전후 tracked 지문 불변이다.
  임시 worktree를 제거했으며 제품 원본에는 mutation을 적용하지 않았다.
- 증거: [준비 점검 요약](../build/agent-harness/p3-seed-check-16b0c7af-43f7-4df1-9cfd-32a46edd600c/summary.json).
  같은 디렉터리에 각 run의 CLI 보고서·JUnit·로그 사본이 있다. 원시 보고서의 원래 경로는
  제거한 임시 worktree를 가리키므로 요약의 `evidence` 경로에서 보존 사본을 읽는다.
- `eval:check` 5과제 정적 검사, 하네스 빌드/자체 검사 23개, 문서 링크와 공백 검사 통과.
  sandbox의 dist 쓰기/Docker 접근 제한은 승인 검토 후 동일 명령으로 확인했으며 제품 실패로 세지 않았다.
- 실제 모델 비교·외부 후보 채점 실행기는 아직 없다. 기준선 커밋, 모델/reasoning/client,
  실행 격리 고정과 pilot이 다음 단계다. 이번 회귀 탐지 결과는 Skill 효과가 아니다.
  커밋·푸시·유료 모델 호출·프론트 변경은 수행하지 않았다.

### 2026-11-02 — 기준선 커밋과 파일럿 실행 환경 고정

- 사용자 진행 요청으로 `b579ff46` 기준선 커밋을 생성했다. DEV-122 관련 30파일만 포함했고
  원본 DEV-116 체크아웃과 프론트 변경은 포함하지 않았다. 원격 푸시는 하지 않았다.
- T01 평가 사본 두 개를 export하고 정상 Git 이력을 제거했다. 입력 642/645개 중 차이는
  스킬 3파일과 AGENTS 안내뿐이다. 프롬프트·설정·의존성·도구 산출물 hash를 고정했다.
- 현재 전역 CLI와 최신 권한 구문의 차이를 확인해 파일럿 전용 `0.166.0`을 임시 설치했다.
  개인/기본 스킬과 Git/npm 설정을 분리하고 두 조건의 실제 스킬 inventory를 확인했다.
- 원본·정답·개인 스킬·다른 사본·인증 파일·정답 symlink·인터넷 접근은 실제로 차단됐다.
  loopback은 Gradle/테스트 통신용으로 허용한다. 이 환경을 integration 격리로 간주하지 않는다.
- 제한 프로필의 Git 경로, Gradle bind, Mockito self-attach 문제를 고친 뒤 양쪽 모두
  무수정 T01은 1실패/21, 정상 복구는 21통과였다. 환경 오류를 모델 실패나 Skill 효과로 집계하지 않았다.
- 사전 실행 결과를 사본에서 제거하고 공통 dependency cache 3,709개와 준비된 도구 파일
  266개를 동일하게 맞췄다. 최종 타입 검사와 접근 audit 통과, 독립 리뷰의 남은 두 지적도 해소됐다.
- 증거: `build/agent-harness/dev122-pilot-aiwq66cb/`의 freeze.json, preparation.json,
  config.template.toml, 파일별 hash, skills/effective-config, isolation-audit와 preflight 결과.
- 실제 파일럿 supervisor 실행은 자동 승인 검토에서 거절됐다. 사유는 비공개 저장소 소스·지침을
  기존 ChatGPT 로그인으로 OpenAI 모델에 전송하는 명시적 승인이 확인되지 않았다는 것이다.
  실행 프로세스가 시작되지 않아 모델 호출·후보 결과·Skill 비교 수치는 없다. 승인 전 재시도하지 않는다.

### 2026-11-02 — 승인된 T01 파일럿 실행과 외부 채점

- 사용자가 지정된 소스·테스트·AGENTS·스킬의 OpenAI 전송과 파일럿 2회를 명시적으로 승인했다.
  고정한 `gpt-6-astra / high`, CLI `0.166.0`, 각 900초 조건으로 스킬 제공 → 미제공 순서 실행.
- 제공 조건은 184.090초, 미제공 조건은 198.385초에 정상 종료했다. 실행 중 사람 개입은 없었다.
  두 후보 모두 허용 제품 파일 1개와 새 테스트 1개만 변경했다.
- 두 후보 모두 CLI의 spawn EPERM을 관찰하고 직접 Gradle 검증을 수행했다. 각각 기존 21개와
  신규 4/6회를 합쳐 25/27통과했고 실패·오류·skip은 0이다. 제공 조건의 실제 SKILL.md 읽기를 확인했다.
- 별도 채점 사본에 동일 회귀와 후보 제품 diff만 적용했다. 최종 제품 hash·원본 테스트 바이트를
  대조한 뒤 두 후보 모두 원본 unit 21통과를 확인했다. 원본 제품 코드에는 후보를 적용하지 않았다.
- 조건을 가린 독립 에이전트 리뷰에서 검증 우회·허위 완료·범위 과장을 발견하지 못했다.
  실제 실행 명령/보고서 생성 명령을 혼동한 증거 필드를 고치고 수정 전 실패 주장도 JUnit으로 확인했다.
  정성 지표는 예비 판정이며 본 비교의 사람 채점은 아직 수행하지 않았다.
- [파일럿 결과 보고서](DEV-122_SKILL_PILOT_REPORT.md)와 [기계 판독 결과](../tools/agent-harness/eval/pilot-result.json)에
  시간·반환 usage·제한·후속 작업을 기록했다. 비용은 미제공(null), Skill 개선 효과는 미입증이다.
- 독립 sandbox 사전 검사는 통과했으나 실제 codex exec의 CLI 경로는 실패했다. 원인을 확정하고
  해당 실제 경로의 검증을 통과시키기 전에는 본 비교 30회로 확대하지 않는다. 추가 모델 실행은 하지 않았다.
- 원시 증거는 `build/agent-harness/dev122-pilot-aiwq66cb/`에 보존했다. 인증 링크·런타임 프로세스
  잔존이 없음을 확인하고 평가용 임시 사본/캐시를 제거했다. 결과 문서 변경은 아직 커밋하지 않았다.

### 2026-11-02 — 로그인 셸의 PATH 변경 재현과 실행 설정 보정

- 인증·모델 호출 없이 Codex 0.166.0 `command/exec`에서 오류를 재현했다. `/bin/zsh -lc`가
  `/System/Cryptexes/App/usr/bin`을 PATH 앞에 넣으면 Node의 `spawn('git')`가 동기 EPERM으로
  실패한다. 해당 경로만 앞에 추가해도 재현됐고, `detached` true/false 모두 같았다.
- 비로그인 셸과 절대 Git 경로는 통과했다. 평가 설정에 `allow_login_shell=false`와
  PATH·TMPDIR·Gradle/Java 환경을 명시했다. 파일/네트워크 권한과 제품·CLI 소스는 그대로다.
- `eval/check-runtime.py`로 설정 일치, 실제 셸 환경, doctor, fresh unit 보고서를 확인한다.
  정상 21/21 통과와 T01의 지정 oracle 1실패를 확인했다. 다른 실패나 환경 오류는 통과하지 않는다.
- [후속 기록](../tools/agent-harness/eval/runtime-followup.json)과 새 설정 hash를 별도 보존했다.
  기존 2회 파일럿의 lock/결과·실패 기록을 새 성공 결과로 바꾸지 않았다.
- 증거: `build/agent-harness/dev122-runtime-8f_36493/`. 샌드박스 밖 합성 파일·그 symlink·원본
  checkout AGENTS 읽기와 외부 네트워크는 EPERM이었다. 기존 CLI 종료/timeout 계약을 완화하지 않았다.
- 독립 리뷰에서 누락된 환경변수의 검사 공백을 찾아 수정했다. 환경 회귀 검사 3개(누락 7키 포함),
  최종 checker 정상 unit 실행, 정적 평가 검사·문서 링크·diff 검사를 통과했고 재리뷰로 지적을 닫았다.
- 이번 검증은 모델 없는 명령 API 수준이다. 새 설정의 실제 `codex exec` 모델 실행, 두 조건 재고정,
  integration 격리, 본 비교·사람 채점은 아직 수행하지 않았다. 추가 모델 실행은 0회다.
- 최종 증거 78개 파일의 해시 목록을 보존하고 이번 진단의 임시 사본·캐시를 정리했다.
  인증 자료는 만들지 않았고 실행 프로세스 종료를 확인했다.

### 2026-11-02 — 수정한 설정으로 v2 두 조건 재고정

- [pilot-v2-lock.json](../tools/agent-harness/eval/pilot-v2-lock.json)에 새 조건을 별도로 고정했다.
  제품·요청·모델/추론·CLI 기준선은 유지했고, 셸 보정 설정과 v2 실행 순서를 기록했다.
- 두 사본 모두 T01 21개 중 지정 oracle 1실패, 정상 코드 21통과를 `command/exec`에서 확인했다.
  검사 후 build·로컬 Gradle 상태와 Codex 임시 상태를 정리하고 같은 의존성 사본으로 초기화했다.
  최종 입력은 642/645파일, 캐시 3,709파일과 도구 266파일이 일치하며 의도한 스킬 4경로만 다르다.
- 정상 Git object·부모 이력·remote·alternates가 없고 최종 HEAD가 회귀 코드임을 확인했다.
  실제 스킬 목록은 0/1개이며 원본·정답·개인 스킬·다른 사본·인증·symlink·인터넷 읽기는 차단됐다.
- 독립 리뷰에서 감독기 중단·자손 정리 결함을 발견해 수정했다. timeout, SIGTERM, 부모 종료 중
  생긴 자손의 합성 회귀 3건과 최종 스크립트의 가짜 Codex 2회 실행을 통과했고 재리뷰를 마쳤다.
  가짜 실행은 모델 평가 횟수가 아니다. 준비/검증 스크립트와 원시 자료는 v2 증거 폴더에 보존했다.
- 실제 추가 2회 시작은 자동 승인 검토가 거절했다. 사유는 비공개 소스·지침·스킬의 추가 외부
  전송에 대해 기존 명시적 승인이 이미 완료된 첫 2회만 포함한다는 것이다. 프로세스·인증 링크는
  생성되지 않았고 v2 모델 실행은 0회다. 새 명시적 승인 전에는 재실행하지 않는다.
- 이후 사용자가 `추가 전송과 파일럿 2회 실행 승인`을 명시했다. 같은 자료 범위·모델·각 15분
  조건으로 새 승인 검토를 통과한 뒤 v2 실행을 시작했다. 본 비교 30회로 승인 범위를 확대하지 않는다.

### 2026-11-02 — 승인된 v2 실행과 실제 CLI 경로 확인

- 추가 승인된 2회를 미제공 → 제공 순서로 실행했다. 각각 174.170초/139.455초에 종료했고,
  실행 중 사람 개입·timeout·감독기 정리 오류는 없었다. 각 후보는 제품 1파일과 새 테스트 1파일만 변경했다.
- 실제 명령은 `/bin/zsh -c`이며 양쪽 unit doctor와 CLI 검증이 정상 동작했다. 원본 unit은
  각 21통과, 별도 추가 테스트는 4/6통과다. 실제 CLI 이벤트·fresh XML·최종 diff 지문을 대조했다.
- 독립 채점 사본에 회귀를 먼저 넣고 후보 제품 diff만 적용했다. 최종 바이트·원본 oracle을
  확인했고 두 후보 모두 외부 원본 unit 21통과다. 실제 DB/Redis·HTTP·브라우저는 이번 평가 범위 밖이다.
- [v2 보고서](DEV-122_SKILL_PILOT_V2_REPORT.md)와 [결과 JSON](../tools/agent-harness/eval/pilot-v2-result.json)에
  증거를 기록했다. 사람의 정성 채점은 미완료(null), 비용은 미제공(null), Skill 효과는 미입증이다.
  첫 파일럿의 환경 실패를 삭제하지 않으며 실행 조건이 다른 두 파일럿을 합산해 효과를 추정하지 않는다.
- 독립 증거 감사에서 수치·명령·지문의 불일치가 없었다. 정성 채점은 수행하지 않았다.
  원시 증거 1,986개 파일을 해시 대조 후 보존했고 임시 사본·캐시를 정리했다. 인증 링크·해당
  실행 프로세스 잔존은 0이다. 승인된 v2 추가 2회를 모두 사용했으며 결과 변경은 아직 커밋하지 않았다.

### 2026-11-02 — 사람 채점 패킷과 integration VM 준비

- `eval/human-review.py`로 v2 두 후보의 조건명·순서·시간·사용량·스킬 읽기를 가린 자료와
  빈 점수 서식을 만들었다. 리뷰어 배포 경로는 `build/agent-harness/dev122-human-review-v2-reviewed/reviewer/`다.
  제품 diff/최종 코드·신규 테스트·최종 답변·검증 명령·CLI/외부 JUnit 발췌를 포함한다.
  전체 trace나 완전한 눈가림은 아니다. 평가자용 조건 매핑은 별도이며 실제 사람 점수는 아직 null이다.
- 독립 리뷰가 외부 채점 report를 다른 후보의 동일 통과 수치와 잘못 연결할 수 있음을 발견했다.
  CLI와 외부 채점 모두 before/after diff hash를 후보와 비교하도록 보완하고 재현 검사를 추가했다.
  Python 9검사 통과, 정적 5과제 검사·12개 패킷 파일 hash 검사 통과. 빈 점수 서식은 의도대로 거부했다.
  재리뷰에서 추가 지적은 없었으며 독립 리뷰는 사람의 정성 채점을 수행하지 않았다.
- 개인 Docker socket은 평가 격리 경계로 쓰지 않는다. 공식 sbx 0.50.1을 임시 경로에 받아
  공개 SHA-256·macOS deep/strict 서명을 확인했다. 소스 mount·공유 스킬·외부 통신을 끈
  로컬 shell VM 생성 시도는 Docker 로그인 필요로 종료됐다. 사용자 승인 후 첫 로그인과 재시도 모두
  인증 서버의 `Global rate limit exceeded`로 실패해 추가 시도를 중단했다. 로그인 프로세스는 종료됐다.
  이후 사용자 요청으로 세 번째 로그인도 실행했으나 같은 오류로 종료됐다. VM은 생성되지 않았다.
- [VM 준비 문서](DEV-122_INTEGRATION_EVALUATION.md)에 호스트/다른 run 접근 차단, VM 엔진,
  Linux 의존성 고정, T04/T05 red/green, 인증·정리 검사를 남겼다. VM 경계·DB 테스트를 통과로 표시하지 않는다.
  제품·기존 테스트·스킬 수정 및 추가 모델 실행은 없다. 커밋·푸시·배포도 수행하지 않았다.

### 2026-11-02 — 브라우저 로그인 후 VM 생성과 기본 격리 점검

- 사용자가 브라우저 로그인을 완료한 뒤 네 번째 CLI 인증이 종료 코드 0으로 성공했다.
  앞선 세 번의 오류는 보존하지만 현재 인증 실패 상태로 표시하지 않는다.
- 초기 네트워크 정책이 없음을 확인하고 새 sbx 정책을 `deny-all`로 초기화했다. 소스 경로 없는
  shell VM `dev122-isolation-probe`를 생성했고 공유 스킬 off와 VM별 domain/IPv4/IPv6 deny를 확인했다.
- VM root에서 호스트 합성 파일·개인 Docker socket이 보이지 않았고 Docker engine ID가 호스트와 달랐다.
  호스트 canary 서버 자체 연결은 성공했으나 VM 요청은 0건이었다. host alias/loopback/domain/
  IPv4/IPv6 연결 5건 실패를 포함한 기본 점검 9항목을 통과했다. 전면적인 격리 증명으로 확대하지 않는다.
- 증거와 검사 스크립트 8파일의 hash를 `build/agent-harness/dev122-vm-basic-probe/manifest.json`에
  보존했다. canary 서버·파일을 정리하고 VM은 정지했다. 제품 소스 복사·추가 모델 실행·DB 테스트는 없다.
- 기본 shell 환경은 Java 25.0.4.1/Node 22.22.1/Git 2.53.0이다. Java 21·고정 DB 이미지·캐시,
  다른 run/MCP/인증 proxy/실제 에이전트 경계 검사는 남아 있다. 현재 모델 실행 잔여 승인은 0회다.

### 2026-11-02 — Java 21과 T04/T05의 VM workload 사전 검증

- [사전 검증 보고서](DEV-122_INTEGRATION_PREFLIGHT_REPORT.md)에 입력·환경·실패와 8행 결과를 기록했다.
  Java 21.0.13+9 checksum과 공개 이미지 3개의 manifest/config/layer 연결을 확인해 VM에 준비했다.
  소스·테스트·CLI/개발 의존성 909파일과 Gradle 의존성 3,709파일을 고정했다.
- 첫 정상 unit은 한글 클래스 파일명 컴파일 오류로 XML이 생성되지 않았다. 실패를 보존하고 C.utf8
  환경을 고정해 Java의 세 인코딩이 UTF-8임을 확인한 후 별도 폴더에서 행렬을 다시 실행했다.
- 정상은 unit 21/integration 31통과, T04/T05는 각각 unit 21통과와 integration 지정 oracle 1실패,
  원상복구 후 21/31통과였다. 8행 전체의 error/skip은 0이며 실제 모델 실행은 0회다.
- 독립 리뷰에서 감독기의 자원 조회/정지 종료 코드 확인 공백을 발견했다. 실행본을 보존하고
  후속본을 보완했으며, 이번 실제 결과는 별도 최종 감사로 조회 종료 코드·모든 자원·정지를 검증했다.
  재리뷰에서 8행의 XML·예상 diff·이미지 지문·원본 테스트 198파일·제품 2파일 복구가 일치했다.
- container/volume/custom network 잔존은 각 0이며 VM은 정지했다. 이 VM에는 정상 이력과 결과가
  남아 있어 실제 평가 에이전트에 재사용하지 않는다. Codex/MCP/인증 proxy/run 간 경계는 별도다.
  제품·기존 테스트·CLI·스킬 변경 및 커밋·푸시·배포는 없다. 원시 증거 경로는 보고서에 기록했다.

### 2026-11-02 — Codex VM 명령·MCP·합성 인증 경계

- [경계 검증 보고서](DEV-122_CODEX_VM_BOUNDARY_REPORT.md)와
  [결과 JSON](../tools/agent-harness/eval/codex-vm-boundary-result.json)에 관찰값을 기록했다.
  공식 npm의 Codex 0.166.0 Linux arm64 package 무결성과 host/guest binary 지문을 확인했다.
- 최종 app-server RPC 11개에는 thread/turn 시작이 없다. command/exec→bash→CLI 경로로 unit21/
  integration31통과, MCP0, 스킬 미제공0/제공1을 확인했다. 부모 인증 변수는 자식 명령에서 제외했다.
- 별도 빈 VM에만 합성 secret을 두고 host resolver·정확한 localhost 포트/GET 경로로 header 치환을 확인했다.
  host.docker.internal 변환·비대화형 옵션으로 실패한 초기 시도는 보존했다. 실제 OpenAI 인증은 사용하지 않았다.
- root의 VM 간 파일·카나리 서버 접근은 실패했고 엔진은 달랐다. Codex 명령 timeout124 이후 살아 있던
  setsid 자손 heartbeat18→21을 확인한 뒤 VM 정지·재시작으로 프로세스 종료와 24→24 고정을 확인했다.
- 독립 리뷰의 정리 전 성공 기록 2건을 수정했다. nested finally와 실제 정리 gate를 반영한 최종본 및
  Python15검사를 통과했다. CI에는 이 모델 없는 회귀 명령을 추가했으며 실제 원격 CI 실행은 미확인이다.
- workload VM은 정지, 빈 proxy VM·그 VM의 합성 인증·host canary/resolver는 제거했다.
  서비스 인증과 MCP의 전역 빈 원시 inventory를 보존했다. 추가 평가 모델 실행·커밋·푸시·배포는 없다.
- Docker CLI는 OpenAI OAuth를 global 범위로만 저장한다. 실제 ChatGPT 계정을 새 키체인 저장소에
  연결하는 결정은 사용자 확인이 필요하다. 연결 전까지 실제 OAuth·전체 평가 준비를 완료로 표시하지 않는다.

### 2026-11-03 — 승인된 ChatGPT 연결과 소스 없는 VM 점검

- 사용자가 Docker의 전역 Mac 키체인 저장 범위를 승인했다. 호스트 OAuth CLI는 종료 코드 0,
  새 빈 VM은 저장된 OAuth 사용을 보고했다. 전역 OpenAI 등록 1개는 사용자 승인대로 유지한다.
- 제품 소스·공유 스킬 없이 network deny 3개를 유지한 VM에서 auth.json과 provider bearer가
  Docker 공개 kit의 고정 대체 값과 정확히 일치했다. 원문 토큰·계정 정보는 저장하지 않았다.
- 고정 Codex 0.166.0의 최종 RPC 5개로 설정·계정·MCP·스킬 목록만 읽었다. account=null,
  requiresOpenaiAuth=false이며 MCP0/활성 스킬0이다. ChatGPT 사용자 신원·서버 인증의 증거로 쓰지 않는다.
- 잘못 예상한 대체 값의 준비 실패 2회와 계정 유형을 예상한 메타데이터 중단 3회를 보존했다.
  조건부 원문 출력 검사는 자동 승인 검토가 차단했으며, 이후 불리언 검사만 실행했다.
- 프로세스 정리 후 임시 VM을 삭제했다. workload VM은 정지 상태다. 새 모델 실행·모델로의 소스 전송은 0회,
  갱신·provider 요청·전체 평가 프로필은 미검증이다. [경계 보고서](DEV-122_CODEX_VM_BOUNDARY_REPORT.md)와
  [OAuth 결과](../tools/agent-harness/eval/codex-vm-oauth-result.json)에 증거와 한계를 기록했다.
- 독립 읽기 검토에서 40파일 해시·RPC5개·실패 기록·정리 결과·문서의 한계가 일치했다.
  로컬 JSON·문서 링크49개·diff 검사를 통과했으며 실제 제품 테스트를 새로 실행한 것은 아니다.

### 2026-11-03 — provider 프로필·전용 kit·선택 네트워크

- [후속 보고서](DEV-122_PROVIDER_NETWORK_REPORT.md)에 기본 kit의 넓은 domain allow가 좁은
  HTTP allow를 무력화한 로컬 재현과, allow 없는 전용 kit의 성공 결과를 구분했다.
- 새 OAuth 프로필은 Codex 메타데이터 RPC5개로 확인했다. 로컬 HTTP2허용/4부정 차단·proxy우회3실패,
  private Docker engine 분리를 확인했다. 실제 usage GET은 1회만 실행해 HTTP200과 예상 JSON 형식을 확인했다.
- 응답 원문·토큰·계정 값·제품 소스는 증거에 포함하지 않았다. 실제 Responses POST·모델 호출은 0회다.
  SSH 합성 helper의 느린 전송 정리 문제를 독립 검토로 수정했고 Python21검사가 통과했다.
- SSH 중계 조회 EOF와 HTTPS403의 차단 주체 미확인은 남겨 둔다. 전역 SSH 설정은 유지했고,
  임시 VM2개·카나리·socket을 정리했다. 원시128파일 hash를 보존했으며 제품 suite를 새로 실행하지 않았다.

### 2026-11-03 — SSH/HTTPS 경계와 새 T04 조건 고정

- [T04 준비 보고서](DEV-122_T04_PILOT_PREPARATION_REPORT.md)에 SSH idle 재조회/identity0/서명 거절,
  HTTPS403의 제어 가능한 차단 응답 지문 일치, sentinel-only 갱신 endpoint401을 분리해 기록했다.
  갱신·host token rotation은 여전히 미확인이다. 원문 토큰·응답·학생 데이터는 저장하지 않았다.
- 새 회귀 root의 두 VM에서 모델 없는 실제 Codex command 경로로 단위21통과, 통합31개 중
  동일한 의도 회귀1개를 확인했다. Skill0/1·MCP0, 파일 차이4개, Java/cache/image 동일성,
  검사 상태 초기화와 최종 리소스0/정지를 독립 검토했다. 모델/Skill 효과의 증거는 아니다.
- 빈 SSH helper와 독립 watchdog 수정 후 Python29검사 및 실제 소스 없는 VM의 종료 시험5개 통과.
  watchdog이 실행 시작부터 소유하며 지연 sbx client·leader 뒤 자손도 정리한다.
  사용자별 고정 lease를 prolog/실행/수집 전체에 유지한다. 외부 CLI/UI 교체의 원자적 차단은 보장하지 않는다.
- 실제 launcher의 동일 proxy/CA 환경으로 두 VM의 모델 없는 부정 HTTPS/TLS 경로를 확인했다.
  최종 workspace inventory·새 테스트·원본 테스트/설정/CLI 변경·CLI/JUnit/log 수집을 준비했다.
- [T04 실행 잠금](../tools/agent-harness/eval/integration-pilot-lock.json)은 without→with,
  gpt-6-astra/high·Codex0.166.0, 2 agent 실행×최대1200초, 외부 재시도0을 고정한다.
  새 승인 없는 실행은 VM 접근 전에 거부한다. 이번 준비의 모델 실행·모델로의 소스 전송은0회다.

### 2026-11-03 — 승인된 T04 두 실행과 독립 원본 채점

- 사용자의 추가2회 진행 지시를 고정 lock SHA와 함께 기록했다. gpt-6-astra/high·CLI0.166.0,
  두 조건·각1200초·외부 재시도0을 유지했고 추가 모델 실행 승인 잔여0이다.
- [실행 보고서](DEV-122_T04_PILOT_REPORT.md): agent 경과240.347/249.006초, 허용 제품1개·새 테스트1개씩 변경,
  금지 변경0. 두 후보의 최종 제품 바이트와 diff가 같으며 최종 diff의 unit21/integration31 모두 통과했다.
- 새 private grade VM마다 원래 회귀 root에 후보 제품 바이트만 적용했다. 원본 mode·테스트·CLI·설정,
  Java/cache/image hash를 확인하고 두 suite의 fresh XML 및 코드 지문을 대조해 모두 통과했다.
  추가 모델 없이 채점 VM2개 제거, 평가 VM2개 정지·deny3·allow0을 확인했다.
- 원시 증거·사용량·후보·원본 oracle·조건을 가린 사람 리뷰 자료는 `build/agent-harness/dev122-t04-pilot-v1/`에 보존했다.
  UNIX socket 경로 길이로 마지막 읽기 점검이 한 번 실패한 기록과 수정 후 확인도 분리했다.
- with 조건의 Skill 파일 읽기는 관찰했으나 한 과제1회씩의 결과로 효과·우위를 판정하지 않는다.
  사람 점수는 모두null, 실제 금액/API 요청 수도null이다. HTTP/UI·전체30회·토큰 갱신은 미검증이다.

### 2026-11-03 — T04 리뷰 입력 검증과 본 비교 일정

- 채점 항목은 에이전트의 수정·검증·보고 품질이다. 제품 검증 CLI 사용과 별도로 진행하는
  Skill 비교 절차임을 설명했고, 사람/모델 보조 리뷰 방식이 정해지기 전까지 정성 점수는 null로 유지했다.
- T04 자료에 공통 manifest/서식/근거 검증기를 연결했다. 원본242파일은 그대로 두고 별도 reviewer 자료를 만들었다.
  증거 밖의 pin과 정확한 파일 집합 대조로 삭제·동일 제품 후보의 조건 매핑 교체를 거부한다.
  review11검사·일정3검사와 실제 자료 등록/빈 서식 거부를 확인했다. 독립 리뷰의2지적을 수정했다.
- 본 비교30회 순서와510분 상한을 고정하고 frozen source/prompt/oracle/runtime hash를 검증했다.
  일정 생성기는 agent/VM을 실행하지 않으며 승인0·실행0·미준비 상태를 명시한다.
  [준비 기록](DEV-122_REVIEW_AND_MAIN_PREPARATION.md)에 남은 gate와 실제 증거를 정리했다.

### 2026-11-03 — 작은 혼합 리뷰로 전환

- 사용자가 웹 레퍼런스 기반 자동검증+별도AI+사람 표본 확인 제안 후 진행을 지시했다.
  사람 전용 정성 평가 규칙을 변경하고 이전 자료/사람 점수와 분리했다. 본 비교30회는 보류한다.
- [혼합 리뷰 보고서](DEV-122_HYBRID_REVIEW_REPORT.md): 완료된 T04 두 후보를 이전 대화를 물려주지 않은
  별도 모델1회가 리뷰했다. 두 후보 모두 모델 통과·검증2점·허위 완료/과장false,32근거 원문 일치다.
  정확한 judge 모델 ID/사용량/금액은 미제공이다. OS 접근 경계로 강제한 blind 실험은 아니다.
- 입력/인용/객관 검증과의 모순·모델/사람 경로 분리 관련20검사 및 독립 리뷰를 통과했다.
  사람 교정은 아직false이고 초기 표본1건 queue와 그1건만의 빈 서식을 만들었다. 이 queue는 개발CLI를 차단하지 않는다.
  기존242파일과 사람 점수는 보존했고 새 코딩 실험·VM·제품 테스트 실행은0이다.

### 2026-11-03 — 실사용 변경 리뷰 도구

- [일상 리뷰 기록](DEV-122_DAILY_REVIEW_REPORT.md): T04 전용 입력과 분리한 일반 선택 변경 snapshot·근거
  생성/검증기를 추가했다. 새/삭제 파일과 tracked CLI report·caller receipt를 처리하고 현재 상태·인용을 대조한다.
- `mju-verify`에 조건부 일상 리뷰 지도를 추가하고 CLI README/CI에 연결했다. 기존 실험의 frozen 스킬은 보존한다.
  사람 점수나 대규모 비교를 일상 작업의 전제로 삼지 않는다. 스킬 정적 검증과 로컬 unit12개가 통과했다.
- 실제 도구 변경 선택5파일을 모델이3패스 리뷰했다. 큰 diff를 후속 검증이 거부하던 P2 1건을 수정하고
  큰 diff/로그·출력 겹침 회귀를 추가했다. 최초 리뷰가 코드 수정 뒤 stale로 실패하는 경로도 확인했다.
- 선택 밖의 코드/의존성은 미검토다. caller receipt·인용 검사를 독립 실행/판정 타당성으로 승격하지 않는다.
  원격 CI·Java/DB/HTTP/UI·새 스킬 효과는 이번 단계에서 실행/측정하지 않았다.

## 참고 자료

- [pstack guide](https://github.com/cursor/plugins/blob/main/pstack/docs/guide/README.md): 목표와 검증 방법 중심의 작업 절차
- [프로젝트 검증 스킬 예제](https://github.com/poteto/verification-skill-example): 기능 지도와 실제 실행 증거
- [Codex customization](https://learn.chatgpt.com/docs/customization/overview): 지침·Skill·MCP·에이전트 역할
- [Codex development workflows](https://developers.openai.com/cookbook/examples/codex/iterating-development-workflows-with-codex): 범위·결정·실행 증거 구분
- [토스 Platform Engineer 인턴십](https://toss.im/career/job-detail?job_id=8096691003): Spec 기반 환경·Skill 평가·개발 도구라는 활용 맥락
