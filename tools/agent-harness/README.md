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
실제 모델 비교 실행기는 아직 없고 기준선 커밋·모델 설정·격리 검증도 별도로 필요하다.
