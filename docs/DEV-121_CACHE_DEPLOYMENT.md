# DEV-121 캐시 배포 준비

후속 이수학점 최적화: [중복 조회 원인과 개선](DEV-121_COMPLETED_CREDIT_PERFORMANCE.md). 합성 NORMAL 조건의 최신 SQL은 off 9회 / shadow 9회 / on 8회다.
확대 예열의 이수학점 p95는 on이 off보다 9/9개 조건에서 5.3~21.9% 낮았으며, 짧은 예열의 일부 악화 결과도 보존한다.
성능 개선 당시 전체 회귀는 834건 중 830건 통과였다. 후속 배포 사전점검 빌드는
836건 중 832건 통과·실패/오류 0·선택 실행 제외 4건이며, MySQL 26.7.0 별도 검증은 아래에 기록한다.

기준: 2026-10-29 / 브랜치: `fix/DEV-121`

**확정한 방향: DEV-121 먼저 배포, DEV-116은 이후 별도 배포.** 이번 릴리스는 기존 V1~V11 파일과
캐시 V14/V15만 포함한다. V12/V13 정책 마이그레이션이나 DEV-116 기능을 포함하지 않는다.

이 문서는 코드에 반영한 배포 준비와 아직 실행하지 않은 운영 절차를 구분한다.
2026-10-29 후속 요청에 따라 운영 설정·스키마 메타데이터·Flyway 이력을 **읽기 전용**으로 확인했다.
운영 학생 데이터 조회, DDL/DML, 마이그레이션 적용, GitHub Variables 변경, 커밋·푸시·배포·재시작은 하지 않았다.
DEV-116 작업 폴더와 브랜치도 수정하지 않았다. 이전 순서 점검에서 V12/V13을 읽은 기록은 보존하지만,
이번 선배포 검증은 DEV-121 파일과 직접 만든 합성 SQL만 사용하며 DEV-116 파일에 접근하지 않는다.

같은 날 [실제 HTTP API 비교](DEV-121_CACHE_HTTP_COMPARISON.md)를 추가했다.
실제 JAR와 별도 MySQL/Redis에서 로그인/JWT 포함 5종 API의 off/shadow/on을 비교했고,
상세 API에 RR을 명시한 뒤 측정 GET 38,880건의 동일 응답과 변경·롤백·Redis 지연을 확인했다.
첫 HTTP 실험에서는 전체 이수학점 p95가 일부 악화했다. 후속 중복 조회 제거와 확대 예열 비교는 위 최신 문서를 참고한다.
짧은 예열의 편차와 운영 환경 검증 한계가 있어 운영 기본 off는 유지한다.

## 마이그레이션 번호

| 작업 | 최종 파일 | 변경 내용 |
| --- | --- | --- |
| 현재 운영 이력 | V1~V11 | 적용된 파일을 이후 JAR에도 보존 |
| DEV-121 | `V14__add_taken_lecture_revision.sql` | 사용자별 수강 revision 컬럼, 기본값 0 |
| DEV-121 | `V15__version_lecture_catalog.sql` | 과목 revision 행과 INSERT/UPDATE/DELETE 트리거 3개 |
| 후속 DEV-116 | 그때의 최신 적용 버전보다 큰 번호 | 기존 미적용 V12/V13 번호 정리는 DEV-116 배포 준비 때 별도로 수행; 현재는 변경하지 않음 |

DEV-121의 기존 V12/V13을 V14/V15로 옮겼다. 두 SQL 파일의 내용은 변경하지 않았다.
DEV-116 작업 폴더의 파일과 갱신한 origin/main·origin/develop의 파일 목록을 함께 비교해 번호 충돌이 없음을 확인했다.
2026-10-29 조회한 두 원격 브랜치는 V11까지이며, DEV-121만 있는 현재 체크아웃은 V12/V13 파일을 포함하지 않는다.

**적용 순서는 V11 → V14 → V15다.** V12/V13이 비어 있어도 DEV-121은 독립적으로 적용할 수 있다.
운영은 사전점검 당시 baseline 0 및 V1~V11만 적용된 상태였다. 이번 JAR에는 기존 V1~V11을 보존한다.
DEV-116을 나중에 합칠 때도 이미 적용한 V14/V15를 포함한 전체 이력을 보존해야 한다.
후속 DEV-116의 미적용 V12/V13은 그때의 최고 버전 이후로 정리한다. 다른 마이그레이션이 없다면
V16/V17이 예시이며 지금 그 번호를 확정하거나 DEV-116 소스를 재명명하지 않는다.
V15까지 적용한 DB에 낮은 V12/V13을 그대로 추가하는 것은 지원하는 배포 경로가 아니다.
이 작업에서는 다른 브랜치를 수정하거나 `out-of-order`, `repair`, 운영 baseline 설정 변경으로 순서를 우회하지 않았다.
이미 적용된 운영 마이그레이션을 임의로 재명명하거나 이력을 고치지 않는다. 실제 배포 시점에는 이력을 다시 확인한다.

`infra/cache/check_release.py`를 배포 workflow의 이미지 발행·SSH 작업보다 앞에 추가했다.
기존 V1~V11 파일 보존, V14/V15 존재, 중복 버전, mode/namespace를 검사한다.
현재 DEV-121 단독 체크아웃은 통과한다. 미적용 V12/V13 계열 파일을 끼워 넣는 경우에는 차단해 선배포 구성을 유지한다.
후속 작업은 최신 적용 버전 이후 번호를 사용할 수 있다. 이 오프라인 검사는 운영 DB의 실제 이력 조회를 대신하지 않는다.
앞서 V12/V13까지 필수로 요구하던 임시 규칙은 제거했다. DEV-116 파일을 소스에 편입하거나 재명명하지 않았다.

## 배포 환경변수

`.github/workflows/deploy.yml`에서 GitHub Actions Repository Variables → job 환경변수 → SSH 환경변수 → Docker `-e`까지 연결했다.

| Repository Variable | 미설정 시 배포 기본값 | 허용 값/의미 |
| --- | --- | --- |
| `TAKEN_LECTURE_CACHE_MODE` | `off` | `off`, `shadow`, `on` 중 하나 |
| `TAKEN_LECTURE_CACHE_NAMESPACE` | `prod-dev121-v3` | 영문·숫자·점·밑줄·하이픈으로 구성한 비어 있지 않은 값 |

두 변수는 비밀정보가 아닌 기능 설정이다. GitHub `vars`에 값이 없으면 위 fallback이 전달된다.
Repository Variables에서 두 변수는 미등록, 기존 `SHARED_MASTER_CACHE_ENABLED=true`는 등록 상태로 확인했다.
조직 수준 변수 조회는 현재 인증으로 불가능했으므로 상속 값의 부재까지 확인한 것은 아니다.
첫 배포 전 repository에 `off` / `prod-dev121-v3`를 명시해서 해석 결과를 확정하는 것을 권장한다. 이번에 등록하지 않았다.
변경된 workflow가 배포에 사용되기 전까지 실행 중인 서버 설정에는 영향이 없다.
잘못된 mode나 namespace는 원격 배포 스크립트가 Docker 정리·이미지 교체·기존 앱 제거 전에 거부한다.

`prod-dev121-v3`는 운영용 신규 namespace다. 같은 데이터 이력을 사용하는 모든 새 인스턴스와 shadow/on 전환에서 같은 값을 유지한다.
배포 ID마다 자동으로 바꾸지 않는다. DB 복구·revision 초기화·테이블 교체 시에는 기존 namespace를 재사용하지 않고 새 값을 정한다.
애플리케이션의 로컬 기본 namespace는 `local`로 유지했다. 운영에서는 workflow가 위 값을 덮어쓴다.

## 초기 검증 기록

- 배포 스크립트 테스트 16건: 세 mode와 사용자 지정 namespace 전달, 잘못된 설정에서 Docker 미호출, 기존 Redis 보존과 실패 경로 통과.
- 캐시 설정 테스트 4건 및 합성 정합성 통합 테스트 30건: 모두 통과. 합계 50건, 실패/오류/제외 0건.
- 실제 일회용 MySQL 8.4.8/Redis 7.4에서 Flyway로 V14/V15를 적용하고 validate 및 재실행 시 추가 적용 0건을 확인했다.
- 기존 사용자 행의 revision 기본값 0, InnoDB, 트리거 3개와 동시 조회·롤백·Redis 장애 등 기존 30개 시나리오를 확인했다.
- `bootJar` 빌드와 패키징된 마이그레이션 파일 목록을 확인했다.

통합 테스트의 `baselineVersion=13`은 정책 테이블이 없는 **합성 fixture만을 위한 설정**이다.
DEV-116의 정책 마이그레이션 자체나 운영 DB 업그레이드 전체를 검증했다는 뜻이 아니며, 운영 설정의 baseline-version 0은 변경하지 않았다.
이 초기 배포 테스트는 Docker 명령을 대체한 로컬 스크립트 실행이다. 후속 SSH 점검도 읽기 전용이며
GitHub 변수 등록·운영 컨테이너 교체는 수행하지 않았다.

초기 실행 증거는 [배포 준비 검증 집계](reviews/dev121-deployment-prep-20261029.json)에 기록한다.
2026-10-27의 전체 회귀·성능 증거는 당시 파일명과 해시를 그대로 보존한다.

## 2026-10-29 운영 사전점검

| 확인 항목 | 결과 |
| --- | --- |
| 실제 배포 | `gha-37565266834-1`, main `5699f0e7435b2f070466ed9de469d44289b383e6`; healthy, restart 0 유지 |
| DB | `MySQL Enterprise - Cloud 26.7.0-cloud`, RR, 관련 테이블 InnoDB |
| Redis | 7.4.11; 기존 HTTP 비교와 Redis 버전 일치 |
| Flyway 이력 | baseline 0, V1~V11 성공; 현재 파일과 checksum 일치; V14/V15는 pending |
| 신규 스키마 | 사용자 revision 컬럼·catalog revision 테이블·lecture 트리거 모두 미적용 |
| 마이그레이션 실행 계정 | prod Flyway는 별도 계정 설정 없이 기존 datasource 계정 사용; CREATE/ALTER/REFERENCES/TRIGGER/SELECT/INSERT/UPDATE 보유 확인 |
| 트리거 조건 | `log_bin=1`, `log_bin_trust_function_creators=1`; 별도 권한 부여나 서버 변수 변경 없음 |
| 캐시 설정 | 운영 컨테이너에는 새 개인 캐시 변수 없음. 아직 구버전이므로 이를 새 코드의 off 상태라고 해석하지 않음 |
| 공유 마스터 캐시 | 운영 true; 기존 개인 캐시 HTTP 성능 비교에서는 false였음 |
| 관측 | 내부 `/actuator/health`, `/actuator/prometheus` 200; HTTP/Hikari/JVM 지표 확인; 새 개인 캐시 지표는 미배포로 없음 |
| 배포 테스트 연결 | main에 필수 status check가 없고 기존 deploy는 `-x test`였음. deploy 자체에서 `clean build`로 테스트하도록 보완 |

현재 패키징된 Flyway 10.10.0 / Connector/J 8.4.0으로 운영에 JDBC read-only 연결을 열어
`info` 및 `validate`만 실행했다. 검증에서 미적용 파일(`*:pending`)만 허용했고 기존 적용 파일은 모두 검증했다.
Spring 앱이나 `migrate`를 실행하지 않았으며 운영 스키마는 그대로다. 이 성공은 운영 DDL 실행 성공을 보장하지 않는다.
Flyway 10.10.0의 MySQL 구현은 8.1보다 최신 DB에 업그레이드 권고를 내므로, 최신 버전의 공식 인증이라고 표현하지 않는다.

기존 테스트 DB는 MySQL 8.4.8이었다. 운영 버전 차이를 확인한 뒤 **MySQL Community 26.7.0**에서도
합성 마이그레이션 1건과 기존 정합성·동시성·롤백·Redis 장애 테스트 31건, 총 32건을 실행해 모두 통과했다.
Cloud 배포판·운영 데이터량·네트워크·공유 캐시 설정까지 동일한 환경이나 운영 부하 시험은 아니다.
8.4.8에서 얻은 API 지연 수치를 26.7.0 운영 성능 수치로 바꾸어 해석하지 않는다.

이전 가정에 따른 V12/V13 → V14/V15 통합 리허설은 기존 사용자 행이 있는 **직접 만든 최소 합성 스키마**에 fixture baseline 11을 두고 진행했다.
8.4.8과 26.7.0 모두 4개 적용, validate, 재실행 추가 0개, revision 기본값 0, 트리거 3개,
과목 INSERT/UPDATE/DELETE revision 증가 및 롤백을 확인했다. 이는 이전 검증 기록이며 현재 선배포 구성은 아래와 같다.
DEV-116 앱 기능·정책 활성화를 검증하거나 실행한 것은 아니다.
운영 baseline 0 설정은 유지한다. V15의 DEFINER는 실제 마이그레이션 계정이 되므로 향후 계정 폐기·권한 변경 때 유지 조건을 확인한다.
MySQL DDL 전체가 애플리케이션 트랜잭션처럼 되돌아간다고 가정하지 않으며, 적용 실패 시 부분 스키마와 이력을 먼저 확인한다.

증거: [운영 메타데이터](reviews/dev121-production-preflight-20261029.json),
[운영 Flyway 읽기 검증](reviews/dev121-production-flyway-readonly-20261029.json),
[GitHub 설정](reviews/dev121-github-preflight-20261029.json),
[관측 경로](reviews/dev121-production-observability-20261029.json),
[이전 규칙의 차단 검사](reviews/dev121-release-gate-20261029.json),
[이전 순서의 8.4.8 리허설](reviews/dev121-migration-order-rehearsal-20261029.json),
[이전 순서의 26.7.0 리허설](reviews/dev121-migration-order-mysql26-20261029.json).

참고: [MySQL 26.7 트리거 권한과 DEFINER](https://dev.mysql.com/doc/refman/26.7/en/create-trigger.html),
[Flyway validate의 검증 범위](https://documentation.red-gate.com/flyway/reference/commands/validate).

## DEV-121 선배포 검증

`CacheMigrationOrderIntegrationTest`는 이제 DEV-116 경로나 파일을 요구하지 않는다.
새 일회용 DB의 최소 합성 user/lecture 스키마에 fixture baseline 11을 두고 실제 V14/V15만 적용한다.
운영 baseline 0 설정이나 이미 적용된 마이그레이션 파일은 변경하지 않는다.

- V11 → V14/V15 두 개 적용, validate, 재실행 추가 적용 0개를 검사한다.
- 기존 합성 사용자 revision 기본값 0, 트리거 3개, 과목 변경의 revision 증가 및 롤백을 검사한다.
- 정책 테이블이 만들어지지 않았는지 확인한다.
- 임시 폴더에서 직접 만든 후속 SQL 두 개를 V12/V13으로 추가하면 validate가 실패하는지 확인한다.
- 같은 합성 SQL을 V16/V17로 옮기면 정상 적용·validate·재실행 추가 0개인지 확인한다.
  이는 후속 번호 정리의 작동 방식 검증이며 실제 DEV-116 SQL의 적용이나 재명명이 아니다.

선배포 기준으로 다시 실행한 결과:

| 검증 | 실제 결과 |
| --- | --- |
| 배포 입력 Python 검사 | 8건 통과; 현재 체크아웃의 CLI 검사 exit 0 |
| 전체 build 및 회귀 | 836건 중 832건 통과, 실패/오류 0, 선택 제외 4건; MySQL 8.4.8 선배포 리허설 포함 |
| MySQL 26.7.0 추가 검증 | 선배포 리허설 1건 + 정합성/동시성/장애 31건 = 32건 모두 통과 |
| 실제 bootJar 내용 | SQL 13개: V1~V11 및 V14/V15, 소스와 바이트 일치, V12/V13 미포함 |
| 패키징된 SQL의 배포 검사 | off/shadow/on 모두 통과 |

증거: [선배포 검증 집계](reviews/dev121-cache-first-verification-20261029.json),
[실제 JAR 검사](reviews/dev121-cache-first-release-gate-20261029.json),
[8.4.8 선배포 리허설](reviews/dev121-cache-first-mysql84-20261029.json),
[26.7.0 선배포 리허설](reviews/dev121-cache-first-mysql26-20261029.json).
이전 순서의 차단 결과나 리허설 JSON은 덮어쓰지 않았으며 현재 계획의 증거로 사용하지 않는다.

기존 성능 측정 결과는 그대로 보존한다. 이번 변경은 배포 검사와 테스트·문서이며 캐시 애플리케이션 로직이나
V14/V15 SQL 내용은 바꾸지 않았다. 실제 운영에서 off → shadow → on으로 전환하는 절차는 계속 필요하다.

## 실제 배포 전에 남은 절차

1. **확정한 선배포 구성 유지:** DEV-121 최종 병합본과 JAR에 V1~V11 및 V14/V15를 포함하고 배포 검사를 통과시킨다.
   DEV-116 완료나 병합을 기다리지 않는다. 실제 배포 시 V11 이후 다른 이력이 생겼는지만 다시 확인한다.
2. 첫 배포의 repository 변수 `off` / `prod-dev121-v3`를 명시하고 최종 병합본의 검사·테스트를 확인한다.
   운영 이력·계정 권한은 이번에 확인했으나 배포 시점에 다시 점검한다. 운영 백업·복구 가능 상태도 배포 담당자가 확인한다.
3. 스키마를 적용·검증한다. **mode가 off여도 새 코드의 DB 매핑을 위한 스키마가 필요하다.**
4. 구 reader/writer로 가는 트래픽과 진행 중 요청을 비우고 모든 인스턴스를 revision-aware 코드로 전환한다. 초기 mode는 off다.
5. 모든 수강목록 writer가 revision을 함께 변경하는지 확인한 뒤 같은 namespace에서 shadow → on으로 전환한다.
   현재 mode/namespace 변경은 재배포·재시작으로 반영한다. shadow에서 DB 결과를 반환하며 불일치·오류·지연·DB 대기를 확인한다.
   운영의 공유 마스터 캐시 true 설정과 초기 예열 구간을 포함해서 관측한다.
6. 문제 발생 시 새 코드를 유지하고 mode를 off로 되돌린다. Redis 전체 flush나 과거 결함 코드로 자동 복귀하지 않는다.

## 검증 재실행

아래 테스트는 로컬 MySQL/Redis 이미지와 격리된 합성 fixture만 사용한다. 원본 스냅샷이나 운영 DB에 연결하지 않는다.

```sh
python3 -m unittest discover -s infra/cache -p 'test_*.py'
python3 infra/cache/check_release.py  # DEV-121 단독 구성으로 exit 0
```

선배포 리허설은 `RUN_DEV121_MIGRATION_REHEARSAL=true`를 지정해 `*CacheMigrationOrderIntegrationTest`를 실행한다.
DEV-116 경로를 전달할 필요가 없고 해당 소스를 읽지 않는다.
`DEV121_MYSQL_IMAGE=mysql:26.7.0`을 지정하면 해당 이미지에서 리허설과 정합성 테스트를 실행한다.
이미지는 미리 준비해야 하며 테스트가 자동으로 pull하거나 기존 DB를 재사용하지 않는다.

운영 메타데이터 점검 도구는 `infra/cache/inspect_production_readonly.py`다. 운영 호스트의 기존 SSH 접속으로
`python3 -`에 표준 입력으로 전달한다. 실행 중인 앱의 접속 정보를 프로세스 메모리에서만 사용하며,
비밀값·원본 docker inspect·사용자 행·원본 권한 문자열을 출력하지 않는다. 설정 변경 기능은 없다.

```sh
DOCKER_HOST=unix:///Users/jinhyeon/.docker/run/docker.sock \
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock \
TESTCONTAINERS_HOST_OVERRIDE=127.0.0.1 \
RUN_TAKEN_LECTURE_CACHE_REPRODUCTION=true \
RUN_DEV121_MIGRATION_REHEARSAL=true \
RUN_TAKEN_LECTURE_CACHE_BENCHMARK=false \
./gradlew test --tests '*ProductionDeploymentWorkflowTest' \
  --tests '*CacheMigrationOrderIntegrationTest' \
  --tests '*TakenLectureCacheConsistencyIntegrationTest' \
  --tests '*TakenLectureCacheConfigurationTest' \
  --offline --no-daemon --no-build-cache
```
