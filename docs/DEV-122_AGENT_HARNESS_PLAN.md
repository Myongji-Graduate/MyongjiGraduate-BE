# DEV-122 개발 검증 하네스와 Skill 평가

## 현재 상태

- 기준일: 2026-11-02
- 백엔드 브랜치: `chore/DEV-122`
- 프론트 브랜치: `chore/agent-harness` (별도 프론트 저장소)
- 기반: 최신 조회한 `origin/develop`, `861228e42f779afa8a517fd3ed05a6bfafc46906`
- 상태: P0/P1 및 P2의 scoped DB·HTTP·UI 계약 검증 완료. P3 과제·명세와 회귀 탐지 점검 완료, 모델 비교는 미실행. UI 입력 호환성 한계는 아래 기록.
- 다음 작업: P3 명세에 따라 기준선 커밋·모델 설정·실행 격리를 고정하고 pilot 2회를 준비한다. 실제 포인터/키보드 UI 입력은 별도 후속 검증.
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
| P3 Skill 비교 평가 | 고정 작업·실행 조건·독립 채점, baseline/skill 비교 보고서 | 과제·명세 준비, 비교 미실행 |
| P4 사용성·배포 준비 | 재현 가능한 가이드, CI 결과, 실제 사용 피드백과 제한 정리 | 미착수 |

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
- 기준선 커밋과 구체적인 모델/추론/client·실행 격리는 아직 미확정이다. 실제 비교 자동 실행기는 없다.

회귀 탐지 점검은 과제의 유효성 증거이며 Skill 효과가 아니다. 5개 과제의 반복 결과를
일반적인 생산성 개선율로 확대하지 않는다. 현재 유료 API 호출과 개선 수치는 없다.

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

## 참고 자료

- [pstack guide](https://github.com/cursor/plugins/blob/main/pstack/docs/guide/README.md): 목표와 검증 방법 중심의 작업 절차
- [프로젝트 검증 스킬 예제](https://github.com/poteto/verification-skill-example): 기능 지도와 실제 실행 증거
- [Codex customization](https://learn.chatgpt.com/docs/customization/overview): 지침·Skill·MCP·에이전트 역할
- [Codex development workflows](https://developers.openai.com/cookbook/examples/codex/iterating-development-workflows-with-codex): 범위·결정·실행 증거 구분
- [토스 Platform Engineer 인턴십](https://toss.im/career/job-detail?job_id=8096691003): Spec 기반 환경·Skill 평가·개발 도구라는 활용 맥락
