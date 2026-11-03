# 졸부 개발 검증 CLI

DEV-122의 개발 검증 도구다. 사람이든 Codex든 같은 명령으로 검증 범위와 실제 실행 결과를 확인한다. 제품의 AI 상담 서비스와 별도 개발 도구이며, 이수과목의 단위·합성 DB/Redis·인증 HTTP 검증을 구분해서 실행한다. 현재 실행 수는 보고서의 실제 XML 집계로 확인한다.

## 설치와 실행

macOS/Linux에서 Node.js 20 이상, Java 21, Git, 저장소 Gradle 8.10 wrapper가 필요하다. `gradlew`와 동일하게 `JAVA_HOME`이 있으면 그 Java를 사용하고, 없으면 PATH의 `java`를 확인한다. `doctor`는 선택한 실행 파일도 출력한다. 런타임 npm 의존성은 없다. TypeScript와 Node 타입은 빌드 때만 사용한다.

저장소 루트에서:

```sh
npm ci --prefix tools/agent-harness --ignore-scripts
npm run build --prefix tools/agent-harness
node tools/agent-harness/dist/cli.js doctor --json
node tools/agent-harness/dist/cli.js list --json
node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline
```

`doctor [suite]`는 실행 위치에서 Git 루트를 찾아 런타임과 선택한 suite의 파일을 확인한다. 기본은 `takenlecture-unit`이다. 컨테이너 suite는 Docker 메타데이터와 로컬 이미지도 확인한다. 앱·컨테이너를 시작하거나 이미지를 내려받지 않는다. 필수 항목이 없으면 종료 코드 1이다. 의존성 캐시의 완전성이나 테스트 성공은 확인하지 않는다.

`verify`는 Gradle을 실행하므로 빌드 파일과 테스트 코드를 신뢰할 수 있는 체크아웃에서 사용한다. `--offline`은 Gradle 의존성의 네트워크 다운로드를 제한하지만 OS 네트워크 샌드박스가 아니며, wrapper 배포 파일도 사전에 준비되어 있어야 한다. 플래그를 생략하면 Gradle이 배포 파일·의존성을 다운로드할 수 있다. 대상은 suite별 고정 클래스이며 임의 셸 명령·테스트 패턴·앱 시작 옵션은 받지 않는다.

기본 제한 시간은 600,000ms, HTTP는 1,200,000ms다. `--timeout-ms N`으로 1~1,800,000ms 사이 값을 지정한다. HTTP 시나리오 자체의 JUnit 제한은 15분이다. 타임아웃에는 실행한 프로세스 그룹을 종료하고 124를 반환한다. Gradle의 0이 아닌 종료 코드를 보존하며, signal 종료도 실패다.

## 검증 수준 선택

| Suite | 범위 | 기준선의 실행 사례 | 추가 준비 |
| --- | --- | --- | --- |
| `takenlecture-unit` | mock Redis·서비스 포트 계약 | 4클래스, 21개 | 없음 |
| `takenlecture-integration` | 실제 합성 MySQL/Redis, transaction·revision·동시성·복구 | 1클래스, 31회(반복 10회 포함) | 로컬 Docker, MySQL/Redis/Ryuk 이미지 |
| `takenlecture-http` | 새 production JAR, 인증된 loopback HTTP, 캐시 모드·변경·장애 | 1개의 긴 JUnit 시나리오 | 로컬 Docker, MySQL/Redis/Ryuk 이미지, bootJar 빌드 |

```sh
node tools/agent-harness/dist/cli.js doctor takenlecture-integration --json
node tools/agent-harness/dist/cli.js verify takenlecture-integration --offline
node tools/agent-harness/dist/cli.js doctor takenlecture-http --json
node tools/agent-harness/dist/cli.js verify takenlecture-http --offline
```

Docker는 로컬 Unix socket만 지원한다. 필요한 이미지는 `mysql:8.4.8`,
통합 suite의 `redis:7.4-alpine`, HTTP의 `redis:7.4.11-alpine`, 공통
`testcontainers/ryuk:0.13.0`이다. 이미지가 없으면 doctor가 실패한다. 이미지 준비는
별도로 수행하고 doctor/verify가 자동으로 pull한다고 기대하지 않는다.

CLI는 선택한 테스트의 opt-in만 활성화한다. 기존 `DEV121_HTTP_BASELINE_JAR`,
`DEV121_HTTP_STEADY`, MySQL 이미지·합성 비밀번호 override는 상속하지 않는다.
HTTP는 매번 `bootJar`를 먼저 빌드하고, 외부 환경을 비운 별도 JVM에 합성 DB/Redis
주소와 임시 인증값을 제공한다. 기존 애플리케이션 profile이나 학생 스냅샷을 사용하지 않는다.
Test JVM의 `user.home`만 실행별 빈 디렉터리로 격리해 개인 Testcontainers 설정이
다른 Docker를 선택하지 않게 한다. 두 fixture도 컨테이너 생성 전에 실제 Docker
endpoint와 CLI가 선택한 endpoint가 같은지 검사한다. Gradle 캐시와 개인 설정은 변경하지 않는다.

각 실행의 컨테이너에는 고유 `mju.harness.run` label이 붙는다. 정상 Testcontainers
정리 후에도 남은 경우 CLI는 **그 실행 label의 컨테이너와 익명 볼륨만** 제거하고
다시 조회한다. 확인 실패나 잔존 컨테이너는 통과가 아니다. 기존 컨테이너나 전역 volume을
prune하지 않는다. Ryuk 자체는 Testcontainers가 관리한다. CLI 프로세스 자체가 SIGKILL된
경우에는 최종 보고서·후처리가 보장되지 않으며 이를 통과로 해석하지 않는다.

## 검증과 증거

각 실행은 `build/agent-harness/<UTC시각-UUID>/`에 새로 만든 전용 JUnit 디렉터리를 사용한다. Gradle init script가 XML 출력 위치를 지정하며 `--rerun-tasks --no-build-cache`로 이전 실행의 성공을 재사용하지 않는다. 다음 자료는 실행 후에도 보존한다.

- `report.json`: 범위, 시작/종료 시각, 명령, Git revision, 변경 지문, 검사 결과, 한계
- `gradle.log`: 실제 Gradle 표준 출력·오류
- `junit/TEST-*.xml`: 선택한 suite의 정확한 클래스 결과
- `junit.init.gradle`: 이번 실행에서 적용한 출력 경로 설정
- HTTP의 `http/report.json`과 앱 로그: 개별 요청·캐시 모드·변경/장애 검사와 측정. 임시 `.properties`는 정리한다.

XML의 클래스·파일명·테스트 수·개별 testcase와 결과 집계를 대조한다. 누락·중복·타 클래스·0개·skip·실패·오류는 통과로 처리하지 않는다. 선택한 클래스의 존재는 필수지만 테스트 수는 기준선에 고정하지 않는다. 반복 테스트는 메서드명과 반복 번호가 포함된 고유 표시명을 사용한다. CLI 표준 출력의 `reportPath`로 원본 보고서를 찾는다.

Git 지문은 `HEAD`와 tracked 파일의 index/working-tree diff에 한정된다. raw diff를 보고서에 저장하지 않는다. **untracked/ignored 파일 내용은 읽거나 해싱하지 않으므로 이 지문이 체크아웃 전체를 증명하지 않는다.** 실행 중 tracked 상태가 바뀌면 결과를 실패로 기록한다. 공개·재현 평가에는 관련 구현을 커밋한 별도 기준선이 필요하다.

## 범위와 한계

도입 기준선 `861228e4`의 suite에는 `TakenLectureCacheTest` 15건, `DeleteTakenLectureServiceTest` 2건, `GenerateTakenLectureServiceTest` 1건, `FindTakenLectureServiceTest` 3건이 선언되어 있다. `list`의 `baselineDeclaredTests`도 이 기준선의 관찰값이다. 테스트는 직접 객체 생성과 Mockito를 사용한다. 추가·삭제 테스트의 통과는 저장 포트 호출을 확인하며 실제 DB의 revision 증가나 최종 졸업 결과까지 증명하지 않는다.

`status: passed`는 선택한 suite 범위의 통과만 뜻한다. HTTP 요청 수를 JUnit 테스트 수로
세지 않는다. HTTP의 성능 측정은 합성 환경의 관찰값이며 운영 성능 향상을 보장하지 않는다.
프론트 새로고침은 별도 프론트 저장소의 UI 계약 검증이며, UI fixture와 백엔드 통과를
합쳐 전체 시스템 E2E 통과라고 부르지 않는다. Skill 효과는 별도 비교 평가가 필요하다.

## CLI 자체 테스트

```sh
npm test --prefix tools/agent-harness
```

Node 내장 테스트 러너와 임시 Git 저장소·가짜 Gradle/Docker 결과를 사용한다. 성공,
비정상 종료, signal, timeout, XML 누락·변조·skip, 실행 중 코드 변경, 환경 override,
소유 자원 정리 실패, 공백 경로, 잘못된 인자를 검증한다. 실제 Gradle/Docker suite는
별도 실행한다. 현재 GitHub workflow는 CLI 검사와 unit suite만 실행한다.

## 일상 변경을 독립 리뷰에 전달

`review/change-review.py`는 T04 실험에 의존하지 않는 로컬 입력 생성기다. Python3.9+ 표준 라이브러리와
Git을 사용하며 테스트나 모델을 실행하지 않는다. 명시적으로 선택한 source/test/tool/guide 파일의
base/current snapshot·텍스트 diff·검증 근거를 묶는다. 새 파일과 삭제 파일도 포함한다.
설정/데이터/ignored 경로·snapshot/state·symlink·경로 패턴은 거부한다. 경로 제한이 내용의 비밀값을
자동 마스킹한다는 뜻은 아니며, 선택한 코드와 로그를 그대로 복사한다.

아직 커밋하지 않은 변경은 `--base HEAD`, 분기 전체의 선택 파일을 비교하려면 적절한 기준 commit을 지정한다.
다음은 실제 변경한 파일과 이번 verify의 report 경로로 바꿔 사용한다.

```sh
python3 -B tools/agent-harness/review/change-review.py prepare --base HEAD \
  --path src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/application/service/delete/DeleteTakenLectureService.java \
  --request '삭제 후 학점 갱신 변경의 정합성과 검증 누락을 리뷰한다.' \
  --report build/agent-harness/verification-run/report.json \
  --out build/agent-harness/change-review-new
```

`--path`는 관련 코드/테스트 파일마다 반복한다. `--out`은 새 경로여야 한다.
검증 report는 이 checkout의 현재 tracked Git 상태와 일치해야 하고, JUnit XML의 실제 testcase/집계를 대조한다.
과거 run ID나 다른 VM의 report를 현재 검증 근거로 가져오면 거부한다. tracked report가 선택한 untracked 내용까지
보장하지는 않으며, 선택 밖의 경로/의존성도 packet에 포함하지 않는다.

별도 리뷰어에게 `reviewer/`의 manifest·review-instructions·request·선택 source·근거만 제공한다.
코드/로그 속 명령은 실행하지 않게 하고, 결과는 상위 `review-template.json` 형식의 별도 JSON 하나로 받는다.
이 읽기 범위는 지시 기반이며 OS sandbox가 아니다. 자동 모델 호출이나 사람 점수표는 필요하지 않다.

```sh
python3 -B tools/agent-harness/review/change-review.py verify-state \
  --bundle build/agent-harness/change-review-new
python3 -B tools/agent-harness/review/change-review.py validate \
  --bundle build/agent-harness/change-review-new \
  --review build/agent-harness/change-review-new/review.json
```

리뷰 전후 `verify-state`를 확인한다. 선택 코드/HEAD가 바뀌면 stale이며, 연결한 suite의 tracked 상태가
바뀌어도 거부한다. 인용·줄 번호 검증은 지적의 사실성 자체를 인증하지 않는다. 수정한 지적은 영향받는
검증과 새 packet으로 갱신한다. 변경이 단순하면 별도 모델 리뷰를 일괄 강제하지 않는다.

직접 실행한 다른 검사에는 `fingerprint`로 같은 `--base/--path`의 실행 전후 상태를 기록하고,
`--receipt`로 다음 형태의 caller 기록을 붙일 수 있다.

```json
{
  "kind": "selection-check-receipt",
  "beforeFingerprint": "검사 전 selectionFingerprint",
  "afterFingerprint": "검사 후 동일한 selectionFingerprint",
  "command": ["python3", "-B", "-m", "unittest", "discover"],
  "exitCode": 0,
  "logPath": "build/agent-harness/check.log",
  "logSha256": "실제 로그 SHA256"
}
```

receipt의 선택 fingerprint와 로그 hash를 확인한다. 종료 결과는 호출자가 기록한 값이며,
독립 실행 인증으로 승격하지 않는다. 일반 리뷰는 결함/미검증 경계 중심이고 Skill 효과 점수와 분리한다.

```sh
python3 -B -m unittest discover -s tools/agent-harness/review -p 'test_*.py'
```

## P3 Skill 비교 평가 준비

과제·조건 격리·채점·예산은 [평가 명세](../../docs/DEV-122_SKILL_EVALUATION_SPEC.md)에 있다.
`eval/tasks.json`에는 평가자 전용 mutation과 oracle이 있으므로 평가 에이전트의 checkout에
이 절과 `eval/`을 포함하지 않는다. 두 조건에는 동일한 제품·테스트·CLI를 제공한다.

```sh
npm run eval:check --prefix tools/agent-harness
```

5개 과제의 source anchor가 각각 한 번만 나타나는지, 참조 테스트/요청 경로, 예산을 검사하고
파일 지문을 출력한다. 파일을 수정하거나 Gradle·Docker·모델을 실행하지 않는다.
`preparation-valid`는 정적 준비 검사 통과이며 실험 실행 가능/Skill 효과 판정이 아니다.
T01 파일럿의 기준선·모델·격리 사전 검사는 [pilot-lock.json](eval/pilot-lock.json)에 고정했다.
승인된 T01 실행 2회와 별도 원본 unit 채점을 완료했다. [파일럿 결과](../../docs/DEV-122_SKILL_PILOT_REPORT.md)에
관찰값과 한계를 기록했다. 두 후보는 통과했지만 실제 에이전트 명령 경로에서 CLI의
`spawn EPERM`이 발생해 직접 Gradle을 사용했다. 후속 진단에서 로그인 셸이 앞에 추가한
제한된 PATH 경로가 원인임을 재현했다. 새 설정의 모델 없는 사전 검증은 완료했지만,
이후 v2 실제 모델 파일럿에서도 보정을 확인했다. 전체 자동 비교·채점 실행기는 아직 제품화하지 않았다.

### 평가용 명령 경로 사전 검사

[codex-unit-config.toml.template](eval/codex-unit-config.toml.template)은 이 Mac의 Codex 0.166.0 /
Node 24.11.0 / Java 21.0.6 경로를 고정한 **unit 평가 전용** 설정이다. 전역 Codex 설정에
덮어쓰지 않는다. `allow_login_shell=false`로 셸 시작 시 PATH 재배치를 막고,
`shell_environment_policy.set`으로 PATH·TMPDIR·Gradle/Java 환경을 고정한다.
권한은 이전 파일럿과 같다. 다른 머신에서는 도구 경로·캐시를 다시 준비하고 검증해야 한다.

Python 3.11+와 사전 준비한 독립 사본이 필요하다. `/private/tmp/<case>/` 아래에
`workspace/`(독립 Git·빌드된 CLI·소스·원본 테스트), `codex-home/`, `gradle-home/`, `tmp/`를
두고 템플릿의 `${CASE_ROOT}`만 실제 절대 경로로 치환해 `codex-home/config.toml`로 저장한다.
평가 명세에 따라 의존성 전용 Gradle 캐시를 준비한다. `auth.json` 또는 인증 링크가 있으면
검사가 거부된다. 이 도구는 사본/회귀를 만들거나 로그인·모델 실행을 수행하지 않는다.

```sh
python3 tools/agent-harness/eval/check-runtime.py \
  --codex /absolute/path/to/codex-0.166.0 \
  --case-root /private/tmp/prepared-case \
  --out /private/tmp/new-preflight-evidence \
  --expect healthy
```

평가자가 T01 회귀를 적용한 별도 사본에는 `--expect T01`을 쓴다. 정상은 21/21 통과,
T01은 21개 중 `malformedAndForeignUserPayloadsFallBack()`만 실패해야 한다. 환경 오류·
다른 실패·skip·비정상 CLI 종료는 사전 검사 실패다. 이 숫자는 고정한 평가 기준선에만 적용한다.
결과 디렉터리는 새 경로여야 하며 `result.json`, 유효 설정, 셸 환경, doctor/verify 출력이 저장된다.
실제 Gradle 로그·XML은 `result.json`의 `reportPath` 옆에 있으므로 사본 정리 전 함께 보존한다.
환경변수 누락·PATH 변경의 판정 회귀 검사는 `python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*.py'`로 실행한다.

`command/exec` → `/bin/zsh -c` → CLI까지 확인한다. 실제 모델이 명령을 선택하는 과정,
전체 격리 감사나 integration/HTTP/UI 검증은 포함하지 않는다. 모델 실행 전 두 조건의 입력과
캐시를 다시 같은 상태로 만들고, 사전 검사의 정답·로그·Git 이력이 에이전트 사본에 남지 않게 한다.
기존 파일럿과 새 설정 hash는 [runtime-followup.json](eval/runtime-followup.json)으로 구분한다.
새 설정의 두 조건 입력·캐시·사전 검증은 [pilot-v2-lock.json](eval/pilot-v2-lock.json)에 고정했다.
v2는 명시적 추가 승인을 받아 완료했다. 양쪽 실제 CLI·외부 원본 unit 21통과로 보정한 실행
경로를 확인했다. [v2 결과](../../docs/DEV-122_SKILL_PILOT_V2_REPORT.md)는 Skill 효과·사람 채점과 구분한다.
셸 설정과 명령 API 계약은 [OpenAI 설정 문서](https://learn.chatgpt.com/docs/config-file/config-reference)와
[App Server 문서](https://learn.chatgpt.com/docs/app-server#command-execution)를 참고했다.

### 파일럿 v2 사람 채점 자료

완료된 T01 v2에 한정한 자료 생성기다. 추가 모델 호출이나 자동 점수 부여를 하지 않는다.
보존한 원시 증거의 diff/source hash, CLI 이벤트·JUnit·외부 원본 결과를 대조한 뒤,
조건·순서·시간·사용량·스킬 읽기를 가린 자료와 빈 점수 서식을 만든다.

```sh
python3 -B tools/agent-harness/eval/human-review.py prepare \
  --evidence build/agent-harness/dev122-pilot-v2-xhz9ygrb \
  --out build/agent-harness/dev122-human-review-v2-reviewed
```

출력은 **새 디렉터리**여야 한다. 리뷰어에게 `reviewer/`만 전달하고 상위
`evaluator-map.json`은 채점 전 공개하지 않는다. 자료의 README와 task를 읽은 뒤
`scores.json`에 실제 사람이 판단·이유·후보 자료의 경로/인용을 작성한다. 이미 기존 결과를
본 리뷰어는 `priorExposure`에 적는다. 코드와 답변으로 조건이 드러날 가능성은 남는다.

```sh
python3 -B tools/agent-harness/eval/human-review.py validate \
  --packet build/agent-harness/dev122-human-review-v2-reviewed/reviewer \
  --scores build/agent-harness/dev122-human-review-v2-reviewed/reviewer/scores.json \
  --map build/agent-harness/dev122-human-review-v2-reviewed/evaluator-map.json
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*.py'
```

빈 서식은 실패가 정상이다. 후보 누락·잘못된 점수형·근거 누락·다른 후보 인용·자료 변경을
거부한다. 이유와 인용을 갖춘 판단 불가(null)는 unresolved로 센다. 검증기 통과가 사람의
신원이나 정성 판단의 타당성을 인증하지 않으며 원본 파일럿 결과를 자동 갱신하지 않는다.

### Integration 평가 VM 준비

[격리 준비 문서](../../docs/DEV-122_INTEGRATION_EVALUATION.md)의 경계 검사를 먼저 마친다.
공식 sbx 0.50.1 파일·서명을 확인했다. 앞선 세 인증 시도는 `Global rate limit exceeded`로
실패했으나, 사용자가 브라우저 로그인을 완료한 뒤 네 번째 CLI 인증은 성공했다.
초기 정책을 `deny-all`로 설정한 뒤 VM 기본 점검 9항목과 Java 21의 T04/T05 사전 검증 8행을 완료했다.
현재 VM은 정지 상태이며 정상 이력·결과가 있어 실제 평가 run에는 재사용하지 않는다.
기존 개인 Docker socket을 평가 에이전트에 전달하지 않는다.

로컬 인증은 사용자가 완료해야 한다. 아래는 처음 준비할 때 사용한 shell VM 생성 명령이며
모델을 실행하지 않는다. 인자에 소스 경로를 추가하지 않는다. 이미 같은 이름의 VM이 있으면
재생성하지 말고 `sbx exec dev122-isolation-probe ...`로 필요한 명령을 실행한다(정지 VM은 시작된다).

```sh
/private/tmp/dev122-sbx-0.50.1/native-extracted/bin/sbx login
# 정책이 아직 초기화되지 않은 새 설치에서만 실행한다.
/private/tmp/dev122-sbx-0.50.1/native-extracted/bin/sbx policy init deny-all
/private/tmp/dev122-sbx-0.50.1/native-extracted/bin/sbx create \
  --name dev122-isolation-probe --skills=off \
  --deny-network '**' --deny-network '0.0.0.0/0' --deny-network '::/0' \
  --cpus 2 --memory 4g --no-progress shell
```

임시 파일이 없으면 [고정 릴리스](https://github.com/docker/sbx-releases/releases/tag/v0.50.1)를
다시 내려받고 [준비 기록](eval/integration-runtime-preparation.json)의 hash·서명을 재확인한다.
기본 shell VM은 Java 25를 제공한다. 사전 검증에서는 별도로 확인한 Java 21.0.13+9와
`LANG=LC_ALL=C.utf8`, VM별 `GRADLE_USER_HOME`, VM Unix Docker socket을 사용했다.
JDK·소스/의존성·이미지는 호스트에서 준비해 `sbx cp`로 옮겼으며 VM의 network deny는 유지했다.
파일·도구 지문과 초기 locale 실패는 [사전 검증 보고서](../../docs/DEV-122_INTEGRATION_PREFLIGHT_REPORT.md)에 있다.

준비된 검증 환경에서 사용한 명령은 다음과 같다. 실제 사전 검증의 환경·명령·원시 보고서는
`build/agent-harness/dev122-vm-workload-2c15mchs/`에 보존돼 있다. 준비 감독기는 증거용 스크립트이며
새 환경의 전체 자동 준비·모델 평가 실행기로 제품화한 것은 아니다.

```sh
node tools/agent-harness/dist/cli.js doctor takenlecture-integration --json
node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline
node tools/agent-harness/dist/cli.js verify takenlecture-integration --offline
```

위 명령의 정상/회귀/복구 결과와 실제 Codex 명령 경로·인증·MCP·run 간 경계 검증은 별도다.
실제 비교 전에 새 VM과 회귀 root를 만들고 조건별 입력·캐시를 다시 고정한다.

### VM 안의 Codex 명령 경로 검사

[check-vm-command.py](eval/check-vm-command.py)는 이미 준비된 Linux VM 안에서만 실행하는
모델 없는 검사기다. [VM 전용 설정](eval/codex-vm-config.toml.template)을 호스트 사용자 설정에
덮어쓰지 않는다. 이 프로필의 내부 full access는 mountless VM와 호스트 network deny를 경계로 사용한다.
`command/exec`는 externalSandbox로 실행하며 검사기는 thread/turn 시작 메서드를 거부한다.

준비 상태: `/opt/dev122/workspace`의 독립 Git·빌드된 CLI·원본 테스트, Java 21,
고정 이미지·Gradle cache, `/opt/dev122/codex`의 고정 Linux Codex 0.166.0,
`/opt/dev122/codex-state/config.toml`, 고정 baseline의 `/opt/dev122/skill-seed` 3파일.
실제 `auth.json` 없이 수행하며 부모의 API/GitHub/MCP 인증 환경변수는 상속하지 않는다.
출력 경로는 새 디렉터리여야 한다. 예:

```sh
sbx exec dev122-isolation-probe python3 /opt/dev122/check-vm-command.py \
  --codex /opt/dev122/codex/package/vendor/aarch64-unknown-linux-musl/bin/codex \
  --skill-seed /opt/dev122/skill-seed \
  --out /opt/dev122/new-command-evidence
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*.py'
```

읽은 실효 설정·환경·MCP/스킬 목록·명령 결과와 cleanup 확인을 저장한다. unit21/integration31과
정리 성공 후에만 result.json을 통과로 쓴다. JUnit은 result.json의 reportPath 옆에 있으므로
VM 삭제 전에 함께 보존한다. 현재 CI의 Python 검사는 회귀 함수와 키 없는 합성 UNIX SSH agent를 검사하며 실제 VM이나 모델을 실행하지 않는다.

최종 21/31·스킬0/1·MCP0, 별도의 합성 proxy·VM 간 접근·timeout 증거는
[경계 보고서](../../docs/DEV-122_CODEX_VM_BOUNDARY_REPORT.md)에 있다. 이후 사용자 승인으로
Docker CLI 0.50.1의 `sbx secret set openai --oauth`를 완료했고 전역 Mac 키체인 등록 1개를 유지한다.
별도 빈 VM의 지정 auth/provider 대체 값과 모델 없는 메타데이터만 확인했다.
[OAuth 결과](eval/codex-vm-oauth-result.json)의 account=null은 서버 인증 성공을 뜻하지 않는다.
위 command 검사 프로필은 연결 전 명령 검사 전용이다. 실제 비교에는 Docker의 ChatGPT provider
경로·대체 값을 반영한 별도 프로필과 서버 인증/갱신 검증이 필요하다. 이번 연결 승인은 추가 모델 실행·
모델로의 소스 전송을 포함하지 않는다. 메타데이터 전용 스크립트와 프로필은 원시 증거 폴더에 보존했다.

### ChatGPT provider와 전용 kit 후보

[OAuth 프로필](eval/codex-vm-oauth-config.toml.template)은 ChatGPT backend와 Docker 공개 sentinel을
명시한다. 연결 전의 command 검사 프로필과 별도 파일이며 호스트 사용자 설정에 설치하지 않는다.
[전용 kit](eval/docker-oauth-kit/spec.yaml)는 이미지 digest·shell 진입점·OpenAI OAuth만 선언하고
네트워크 allow를 추가하지 않는다. 기본 Codex kit의 plain domain allow 위에 HTTP allow를 추가하면
경로가 제한되지 않는 것을 실제 canary로 확인했다.

```sh
sbx kit validate ./tools/agent-harness/eval/docker-oauth-kit --offline
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*.py'
```

macOS 제한 셸은 합성 UNIX socket bind도 차단할 수 있다. 이 경우 환경 실패를 테스트 통과로
바꾸지 말고 허용된 로컬 실행 환경에서 검사한다. helper의 전체 읽기 deadline과 종료 정리도 검사한다.

호스트의 sbx 호출은 [EmptySshAgent](eval/empty_ssh_agent.py) context의 `environment()`를 각 subprocess의
env로 사용하고 생성부터 실행·정리까지 유지한다. 전역 SSH 설정은 바꾸지 않는다.
UNIX socket parent는 긴 증거 폴더와 분리해 짧은 `/private/tmp` 경로를 사용한다.
첫 VM 중계 EOF는 cached connection을 유지하도록 고친 후 재조회·identity0·합성 서명 거절을 통과했다.
[provider 보고서](../../docs/DEV-122_PROVIDER_NETWORK_REPORT.md)의 초기 증거와
[T04 준비 보고서](../../docs/DEV-122_T04_PILOT_PREPARATION_REPORT.md)의 후속 결과를 구분한다.
갱신 endpoint401과 실제 모델 경로는 미검증이다.

### T04 제한 파일럿 실행 준비

후속 [T04 결과](../../docs/DEV-122_T04_PILOT_REPORT.md): 승인한2회가 완료됐으며 후보와 독립 원본
oracle의 unit21/integration31이 두 조건 모두 통과했다. 아래는 당시 잠금과 실행 계약이다.
승인 기록·실행 디렉터리를 재사용해 모델을 다시 실행하지 않는다.

[실행 잠금](eval/integration-pilot-lock.json)은 새 두 회귀 VM, 동일 프롬프트/런타임,
without→with 순서, gpt-6-astra/high, 최대2 agent 실행×20분을 고정한다.
원시 증거와 준비된 `run-approved-t04.py`, `launch-agent.py`, `collect-candidate.py`는
`build/agent-harness/dev122-integration-pilot-o9ljg88z/`에 있다. 활성 VM과 큰 입력은 로컬 `/private/tmp`에 유지한다.
기존 모델 승인4회는 모두 사용했다. 기본 metadata 검사는 모델을 실행하지 않는다.

```sh
python3 -B /private/tmp/dev122-integration-pilot-o9ljg88z/run-approved-t04.py \
  --repo "$PWD" --sbx /private/tmp/dev122-sbx-0.50.1/native-extracted/bin/sbx
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*.py'
```

실제 실행에만 `--execute --approval <승인기록.json>`을 추가한다. 승인 기록은 사용자의 새 명시적 승인,
정확한 pilot lock SHA256, 최대2회·각1200초와 일치해야 한다. agent 1회 안의 API 요청 수를 1개로 제한한다는 뜻은 아니다.
승인 없는 실행을 거부하고 실행 디렉터리를 다시 사용하지 않는다. 실행 실패 후 자동으로 재시도하지 않는다.

[vm_job.py](eval/vm_job.py)의 `lease(name)` descriptor를 prolog→`supervise(..., lease_descriptor=fd)`
→차단 상태의 결과 수집→정지까지 유지한다. watchdog은 sbx client 그룹을 종료하고 VM 정지·deny 복원을
확인한 뒤 결과를 쓴다. 고정 사용자별 `/tmp` lease는 다른 TMPDIR에도 같다. 외부 CLI/UI의 동시 이름 교체는
협력 예약 범위 밖이며 UUID가 달라지면 중단한다. `sbx inspect <UUID>`는 지원하지 않아 사용하지 않는다.

launcher는 인증정보 없는 Docker 고정 proxy와 CA만 전달하고, 실제 두 VM에서 같은 env 빌더의 TLS/부정 GET을 확인했다.
수집기는 최종 workspace inventory와 소스/새 테스트/기존 테스트·설정·CLI 바이트 및 bounded CLI/JUnit/Gradle
증거를 보존한다. 이후 별도 새 합성 grade VM에서 원본 테스트/CLI로 재검증한다. 전체30회 비교 실행기는 아직 미완성이다.

완료된 외부 채점 실행기와 기록기는 `build/agent-harness/dev122-t04-pilot-v1/external-grading/executed.py`,
같은 증거 폴더의 `record-results.py`에 보존했다. 원래 회귀 root에 후보 제품 바이트만 적용하고,
원본 테스트/CLI/config, runtime/image hash, fresh JUnit과 diff SHA를 검사했다.
`human-review/README.md`부터 조건을 가린 사람 채점을 시작할 수 있다. 사람 점수는 아직 null이다.

### T04 리뷰 입력과 본 비교 일정

T04 자료는 기존 원본을 유지한 채 공통 채점 서식에 등록했다.
현재 reviewer 폴더는 `build/agent-harness/dev122-review-main-preparation/t04-review/reviewer/`다.
새 등록이 필요하면 아래 `--out`을 아직 없는 경로로 지정한다. 같은 출력 경로는 덮어쓰지 않는다.

```sh
python3 -B tools/agent-harness/eval/human-review.py prepare-t04 \
  --evidence build/agent-harness/dev122-t04-pilot-v1 \
  --out build/agent-harness/t04-review-new
python3 -B tools/agent-harness/eval/human-review.py validate \
  --packet build/agent-harness/dev122-review-main-preparation/t04-review/reviewer \
  --scores build/agent-harness/dev122-review-main-preparation/t04-review/reviewer/scores.json \
  --map build/agent-harness/dev122-review-main-preparation/t04-review/evaluator-map.json
```

기본 신뢰 pin은 `eval/t04-review-source.json`에 있으며 증거 밖에서 원본 결과/보존 manifest를 고정한다.
다른 출처는 별도로 신뢰한 `--source-preservation-sha256`을 지정한다. 서식은 사람이 판단한 값·이유·
같은 후보의 path/quote를 작성한다. 빈 서식은 `human reviewer attestation missing`으로 거부하는 것이 정상이다.
검증기는 값의 근거 연결을 확인하며 실제 사람 신원이나 정성 판단의 타당성을 인증하지 않는다.

```sh
python3 -B tools/agent-harness/eval/main-plan.py validate \
  --schedule tools/agent-harness/eval/main-schedule.json
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*review.py'
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_main_plan.py'
npm run eval:check --prefix tools/agent-harness
```

본 비교 일정은30회·최대510분의 제안이다. 새 파일이 필요하면 `main-plan.py generate --schedule <새 경로>`를 쓴다.
생성/검증은 agent나 VM을 시작하지 않으며 현재 승인·실행은0이다. 전체 입력/환경·과제별 준비와
리뷰 방식·추가 전송 승인이 남아 있다. [준비 기록](../../docs/DEV-122_REVIEW_AND_MAIN_PREPARATION.md)에 범위를 정리했다.

### 별도 모델 리뷰와 사람 표본 확인

현재 흐름은 자동검증→조건명을 가린 별도 모델 리뷰→미확정/문제 항목과 초기 표본의 사람 확인이다.
T04 자료로 이 흐름을 확인했으며30회 본 비교는 보류했다. 모델 점수와 사람 점수는 별도다.

```sh
python3 -B tools/agent-harness/eval/model-review.py prepare \
  --evidence build/agent-harness/dev122-t04-pilot-v1 \
  --out /private/tmp/dev122-model-review-new
```

입력 생성은 모델을 호출하지 않는다. Codex의 별도 리뷰 context/agent에 생성된 `reviewer/`의
manifest·rubric·task와 후보 파일만 제공하고 `model-score-template.json` 형식으로 별도 JSON을 작성하게 한다.
조건 map·이전 대화·부모 폴더를 제공하지 않는다. agent가 소유할 파일은 점수 JSON 하나로 제한한다.
이 지시 기반 읽기 제한을 OS sandbox라고 표현하지 않는다. T04 외 자료는 아직 이 생성기의 지원 범위가 아니다.

현재 보존한 모델 판정은 아래처럼 확인한다.

```sh
python3 -B tools/agent-harness/eval/model-review.py validate \
  --packet build/agent-harness/dev122-hybrid-review-v1/reviewer \
  --scores build/agent-harness/dev122-hybrid-review-v1/model-scores.json \
  --map build/agent-harness/dev122-hybrid-review-v1/evaluator-map.json
```

새 사람 queue는 `validate` 대신 `queue`를 선택하고 같은 인자에 새 `--out` JSON 경로를 추가한다. 빈/근거 없는 값은 거부하고,
판단 불가 null은 이유·관련 인용과 함께 허용한다. 원본 검증 실패를 모델 pass로 덮어쓰거나
모델 packet/평가자를 사람 validator로 제출하는 경로도 거부한다.
검증기 통과는 판정의 타당성·사람 교정을 증명하지 않는다. 관련 검사는 다음으로 실행한다.

`model-review.py human-sample`은 검증된 모델 결과와 queue에 따라 선택한 후보만의 빈 사람 서식을 만든다.
같은 `--packet`, `--scores`, `--map`에 새 `--out` 디렉터리를 지정한다. AI 판정값은 사람 입력에 넣지 않는다.
현재 표본1건의 서식은 `build/agent-harness/dev122-hybrid-review-v1/human-sample/reviewer/`다.
사람이 작성한 뒤 `human-review.py validate`로 해당 폴더·scores·상위evaluator-map을 검사한다.

```sh
python3 -B -m unittest discover -s tools/agent-harness/eval -p 'test_*review.py'
```

[혼합 리뷰 결과](../../docs/DEV-122_HYBRID_REVIEW_REPORT.md)에 실제 두 후보 판정·표본1건·범위를 기록했다.
