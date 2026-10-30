# DEV-121 개인 수강내역 캐시 성능 개선

후속 이수학점 최적화: [중복 조회 원인과 개선](DEV-121_COMPLETED_CREDIT_PERFORMANCE.md). 합성 NORMAL 조건의 최신 SQL은 off 9회 / shadow 9회 / on 8회다.
확대 예열의 이수학점 p95는 on이 off보다 9/9개 조건에서 5.3~21.9% 낮았으며, 짧은 예열의 일부 악화 결과도 보존한다.
최신 전체 회귀는 834건 중 830건 통과·실패/오류 0·선택 실행 제외 4건이다.

작업일: 2026-10-27 (Asia/Seoul) / 현재 브랜치: `fix/DEV-121` (2026-10-29 이름 변경)

기술별 선택 이유와 코드 읽기 순서는 [공부용 가이드](DEV-121_CACHE_STUDY_GUIDE.md)에 정리했다.

2026-10-29 배포 준비에서 캐시 마이그레이션을 V14/V15로 옮기고 운영 workflow에 mode/namespace 전달을 추가했다.
SQL 내용은 유지했고, 배포·캐시 설정·Flyway 정합성 테스트 50건과 bootJar 빌드가 통과했다.
아래 전체 회귀·성능 수치는 2026-10-27 실행 기록이다. 현재 번호와 배포 순서는 [배포 준비](DEV-121_CACHE_DEPLOYMENT.md)를 따른다.

후속 [실제 HTTP API 비교](DEV-121_CACHE_HTTP_COMPARISON.md)는 성능 GET 38,880건의 동일 응답을 확인했다.
이때 상세 API의 RR 경계 누락을 찾아 수정했다. 수강내역은 on에서 9/9개 조건이 개선됐지만,
전체 이수학점은 4/9개만 개선됐고 최악 조건은 p95 +18.7%였다. 서비스 내부 수치로 전체 API 개선을 일반화하지 않는다.
HTTP 별도 실행 통과 후 전체 회귀는 831건 중 827건 통과·실패/오류 0·선택 실행 제외 4건이다.

## 느렸던 이유와 변경

첫 정합성 개선(v2)은 hit에도 사용자·수강 revision·과목 정보 SQL 3회와 Redis 조회가 필요했다.
DB 직접 조회는 사용자 + 수강목록/과목 JOIN의 SQL 2회였다. 작은 목록에서는 JOIN 생략의 이득보다 추가 비용이 컸다.

이번 v3는 다음을 변경했다.

- 사용자·수강 revision·과목 revision을 같은 DB 스냅샷의 SQL **1회**로 조회한다.
  ORM 엔티티의 오래된 revision 대신 SELECT의 scalar 값을 별도로 사용한다.
- 캐시에 과목명·학점·동일대체 코드·폐강 상태도 저장한다. 학생별 인증정보·비밀번호·학번·이름은 저장하지 않는다.
- 과목 INSERT/UPDATE/DELETE 때 DB 트리거가 `lecture_catalog_revision`을 같은 트랜잭션에서 올린다.
  정상 DML을 사용하는 직접 SQL 적재도 포함한다. 트랜잭션 테이블을 전제로 하며 버전 테이블은 InnoDB다.
- 키는 `takenLectures:v3:{namespace}:{userId}:{takenRevision}:{catalogRevision}`이다.
  과거 과목 버전의 늦은 게시가 최신 과목 버전의 캐시를 덮어쓰지 않는다.
- 프로세스마다 최대 **128개 버전 키**, TTL 10분의 메모리 LRU를 둔다. 설정 `local-entries: 0`이면 끌 수 있다.
  개수 제한이며 바이트 제한은 아니다. 이전 revision도 이 예산에 포함된다.
- 메모리 hit도 반드시 DB에서 두 revision과 현재 사용자를 먼저 읽는다. 동일 버전의 불변 스냅샷만 재사용한다.
  메모리 miss는 Redis, Redis miss/장애는 DB로 간다. Redis hit는 검증·JSON 복원 후 메모리에 보관한다.
  DB에서 새로 읽은 값은 성공한 읽기 트랜잭션의 커밋과 Redis 게시 성공 후 메모리에 보관한다.
- Spring이 명시한 읽기 전용 REPEATABLE READ/SERIALIZABLE 트랜잭션만 캐시를 쓴다.
  쓰기·비트랜잭션·READ COMMITTED·격리 수준이 명시되지 않은 DEFAULT 요청은 DB로 우회한다. shadow는 메모리를 우회하고 항상 DB를 기준으로 비교한다.

| 경로 | Hibernate에서 센 데이터 조회 SQL | Redis 접근 |
| --- | ---: | --- |
| 구 JDK 캐시 warm 비교군 | 0 | 1회 |
| v2 membership warm 비교군 | 3 | 1회 |
| v3 Redis hit, 메모리 비활성 | 1 | 1회 |
| v3 메모리 hit | 1 | 없음 |
| v3 cold 또는 Redis 장애 fallback | 2 | miss/실패 시도 후 DB |
| DB 직접 | 2 | 없음 |

추가 확인에서 Connector/J 8.4.0의 `Connection.getTransactionIsolation()`이 서버에 SELECT를 보냄을
로컬 드라이버 소스와 실제 서버 계측으로 확인했다. v2의 이 메타 조회는 위 Hibernate SQL 3회에 포함되지 않는다.
v3는 [Spring의 현재 트랜잭션 격리 수준](https://docs.spring.io/spring-framework/docs/6.1.13/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html)을 사용하여 이를 제거했다.
기본 격리 수준을 추정해 캐시를 켜지 않으며, 수동으로 물리 연결의 격리 수준을 변경하는 경로는 지원하지 않는다.
서버 `Com_select` 증가가 warm 조회에서 **1**인지도 실제 assertion으로 검증했다.
이는 이미 시작된 트랜잭션 안의 값이며, 모든 경로에 공통인 트랜잭션 시작/설정/종료 명령까지 1회라는 의미는 아니다.

Redis 지연 200ms 제한, 동시 Redis 호출 16개, 실패 후 5초 우회 및 복구 probe는 유지한다.
메모리 hit는 Redis가 중단되어도 DB revision을 확인한 동일 버전을 반환할 수 있다.
DB의 버전 확인이 실패하면 메모리에 값이 있어도 반환하지 않는다.

## 검증과 측정

전체 **822건 중 820건 통과, 실패/오류 0건, 기존 opt-in 벤치마크 2건 제외**.
캐시 관련 **50건**(통합 30 + 단위 15 + 설정 4 + 성능 실험 1)은 모두 실행·통과했다.
당시 V13(현재 V15)의 실행 지문을 두 통합 suite에서 확인했고, 과목/버전 테이블 InnoDB 및 트리거 3개를 실제 검사했다.
false 플래그와 없는 Docker socket의 [비활성 가드](reviews/dev121-v3-disabled-guard-20261027.json)는 23건을 모두 건너뛰었다.

성능은 **같은 코드로 두 실행**을 보관한다. 각각 207개 조건별 집계, 합성 요청 **16,959건**, 오류/불일치 0건이다.
아래 두 표는 동시성 4, 반복 3회의 p95 중앙값(ms)이다. 다른 날짜의 실행값을 서로 나누지 않았다.

**성능 실험만 실행한 별도 테스트 JVM**

| 과목 수 | v2 membership | v3 Redis만 | v3 메모리 포함 | DB 직접 |
| --- | ---: | ---: | ---: | ---: |
| 30 | 8.54 | 5.29 | 5.04 | 6.37 |
| 60 | 7.79 | 5.58 | 5.02 | 5.40 |
| 120 | 9.40 | 4.96 | 4.65 | 8.29 |

단독 실행에서 v3 메모리 warm은 **9/9개 조건에서 v2보다**, **9/9개 조건에서 DB 직접보다** 빨랐다.
DB 직접 대비 p95 중앙값 감소는 약 **7~51%**,
v2 대비 감소는 약 **31~59%**였다.
120과목·동시성 4에서는 DB 직접 8.29ms → v3 4.65ms로 약 44% 감소했다.
최대 개별 요청 지연은 78.80ms였다.

**전체 회귀와 함께 실행한 테스트 JVM**

| 과목 수 | v2 membership | v3 Redis만 | v3 메모리 포함 | DB 직접 |
| --- | ---: | ---: | ---: | ---: |
| 30 | 8.51 | 5.67 | 5.31 | 7.90 |
| 60 | 8.87 | 6.46 | 5.05 | 8.25 |
| 120 | 10.21 | 7.42 | 4.86 | 8.31 |

전체 회귀 동시 실행은 v2 대비 9/9개, DB 직접 대비 9/9개 조건에서 빨랐다.
DB 대비 비는 0.54~0.98배였고, 20% 이상 악화한 조건은 0개였다.
최대 개별 요청 지연은 54.34ms였다.
Java 할당·GC/JIT·호스트 부하·실행 순서가 다른 결과를 낼 수 있으므로 절대값/백분율을 운영 SLA로 사용하지 않는다.
단독 실행의 결과로 전체 회귀 실행을 덮어쓰지 않았다. 모든 부하·데이터 크기에서 속도가 우월하다고 일반화하지 않는다.

| 과목 수 | 구 JDK 키 Redis bytes | v3 키 Redis bytes |
| --- | ---: | ---: |
| 30 | 10312 | 6232 |
| 60 | 20552 | 12376 |
| 120 | 41032 | 24664 |

과목 정보를 포함하므로 v2보다 payload가 커졌다. Java 메모리 LRU 공간도 추가된다.
Redis 단일 키 크기 비교를 전체 메모리 절감으로 해석하지 않는다. 실제 hit율·heap·GC는 운영 관측이 필요하다.

[전체 검증 집계·실행 소스 해시](reviews/dev121-v3-verification-20261027.json),
[전체 회귀 JVM의 207개 측정값](reviews/dev121-v3-benchmark-20261027.json),
[단독 JVM의 207개 측정값](reviews/dev121-v3-benchmark-isolated-20261027.json)을 보관했다.

실험은 일회용 MySQL 8.4.8/Redis 7.4, 손으로 만든 학생·서로 다른 30/60/120과목 데이터만 사용했다.
실제 계산기는 자유선택 합성 과목과 고정된 빈 정책 포트로 실행한다. 다른 기존 학사 정책 테스트도 회귀 실행한다.
원본 스냅샷·개인 학생 데이터는 사용하지 않았다.
초기 v3 실험의 migration 계정 권한, fixture 필수 컬럼/엔티티 범위, Redis 독립 연결 재복구 타이밍을 수정했고,
그 후 숨은 JDBC 메타 조회까지 제거한 현재 소스에서 전체 회귀와 단독 성능을 다시 실행했다.

동시성 1/4/8에서 warm 경로 6종을 조건별 3회, 각 96요청으로 순서를 회전하여 비교했다.
구 JDK 형식과 v2는 같은 실행에서 이전 정상 조회 알고리즘을 재현한 비교군이다. 과거 앱 바이너리의 배포 비교는 아니다.
v2는 신규 캐시의 fallback으로 사용되지 않으며 benchmark 전용 클래스다.
cold/Redis 장애는 조건별 1회, 읽기·실제 추가/삭제 혼합은 조건별 3회(32조회+8변경) 실행했다.

전체 필드 정규화, SQL 횟수, local_hit, 지연, DB 대기 표본, JVM CPU/heap 차이를 기록했다.
local warm은 요청당 SQL 1회 및 모든 요청의 local_hit를 assertion으로 검증한다.
HTTP/auth를 포함한 전체 API 응답시간·운영 용량 실험은 아니다. GC와 JIT/로컬 실행 순서의 영향이 있다.
메모리 hit율은 사용자 수·revision 변경 빈도·128키 예산에 따라 달라지므로 warm 수치를 모든 요청에 적용하지 않는다.

정합성 검증에는 동시 조회·롤백 반복, 독립 앱 컨텍스트 2개, 실제 JVM의 커밋 직후 종료,
실제 Redis 지연·재시작/연결 복구, 조회/게시 오류, 잘못된 payload, 권한 검사, 추가/삭제/재업로드를 포함한다.
추가로 과목 버전의 INSERT/UPDATE/DELETE 및 롤백, 과거 과목 reader의 늦은 게시,
ORM의 오래된 managed revision, 메모리의 TTL/LRU/커밋 경계,
메모리 hit 후 다른 앱의 수강 변경·과목 변경·DB 버전 행 소실을 검증한다.

## 운영 전환 조건과 한계

기본 모드는 계속 `off`다. 이는 안전한 schema/writer 전환을 위한 초기 상태다.
운영 DB/애플리케이션 실행·커밋·푸시·배포는 수행하지 않았다.

1. V14(학생 revision)와 V15(과목 revision·트리거 3개)을 마이그레이션 절차로 적용하고,
   `lecture_catalog_revision.id=1`과 세 트리거의 정의·존재를 확인한다. 모든 관련 테이블이 InnoDB여야 한다.
   MySQL 외 DB에서는 이 마이그레이션을 그대로 쓰지 않는다. DEV-116의 V12/V13과 번호 충돌은 해소했다.
   2026-10-29 DEV-121 선배포로 결정했다. V11에서 V14/V15만 적용하며 DEV-116 완료를 기다리지 않는다.
   후속 DEV-116의 미적용 V12/V13은 그때의 최신 적용 버전 이후로 정리한다. 이번 작업에서 DEV-116은 수정하지 않는다.
2. 마이그레이션 계정에는 적절한 DDL/트리거 생성 권한과 유지되는 DEFINER가 필요하다.
   실제 테스트에서도 일반 앱 계정은 binary log의 트리거 생성 권한 검사에 거부됐고,
   동일한 일회용 DB의 별도 migration용 root 연결로 DDL만 수행했다. 앱 조회/변경은 별도 합성 앱 계정으로 실행했다.
   운영 앱에 관리자 권한을 주거나 DB 보안 설정을 완화하는 변경은 포함하지 않는다.
   [MySQL CREATE TRIGGER](https://dev.mysql.com/doc/refman/8.4/en/create-trigger.html),
   [binary log와 저장 프로그램 권한](https://docs.oracle.com/cd/E17952_01/mysql-8.4-en/stored-programs-logging.html).
3. 구 reader/writer 트래픽을 중지·drain하고 모든 학생 writer를 revision-aware 코드로 전환한다.
   이전 캐시 키와 공유되는 rolling 전환은 지원하지 않는다. 새 namespace에서 `shadow`로 검증한 뒤 `on`을 선택한다.
4. `lecture`를 바꾸는 모든 일반 DML은 트리거를 유지해야 한다. `TRUNCATE`/테이블 교체/restore/트리거 제거·비활성화,
   revision 초기화는 정상 DML 경로가 아니다. 이때는 트래픽을 비우고 `off`로 전환한 뒤 트리거·버전 상태를 재검증하고,
   새 namespace의 shadow부터 재시작한다. [MySQL TRUNCATE](https://dev.mysql.com/doc/refman/8.4/en/truncate-table.html).
5. 카탈로그 전체 버전이 바뀌면 개인 캐시의 이전 과목 버전 키가 모두 논리적으로 만료된다.
   다수 과목의 대량 적재는 단일 revision 행 갱신과 cold 부하를 만들 수 있다. 이 쓰기 처리량은 이번 측정 범위 밖이다.
   과목 변경이 드물다는 운영 조건을 확인하고, 빈번한 카탈로그 변경 환경은 별도 평가한다.
6. 오류/불일치/부하 증가 시 revision-aware 코드를 유지하며 `off`로 되돌린다.
   v2/v3의 과거 키는 TTL로 정리하고 전체 Redis flush는 하지 않는다. 동일 namespace/버전 값을 DB 복구 뒤 재사용하지 않는다.
7. 개인 수강내역의 과목 정보에 대한 계약이다. 별도의 졸업요건 공유 캐시·정책 변경은 기존 버전 관리 범위다.
   과거에 잘못 저장된 학점 요약을 자동 일괄 복구하지 않는다.

메트릭 `takenlecture.cache{outcome=local_hit}`를 추가했다. `hit`에는 Redis 및 local hit 모두 포함되므로 두 지표를 합산하지 않는다.
나머지 hit/miss/shadow/mismatch/error/fallback/bypass/transaction_bypass/saturated/load/put과 load timer는 유지한다.
학생 ID·과목 ID를 metric label에 넣지 않는다.

## 재실행

로컬에 이미 준비된 `mysql:8.4.8`·`redis:7.4-alpine` 이미지만 사용하며 자동 pull하지 않는다.
실행 플래그와 Docker 환경은 [기존 계획서](PERSONAL_TAKEN_LECTURE_CACHE_FIX_PLAN.md#재실행)를 따른다.

```sh
RUN_TAKEN_LECTURE_CACHE_REPRODUCTION=true \
RUN_TAKEN_LECTURE_CACHE_BENCHMARK=true \
./gradlew test jacocoTestReport --offline --no-daemon --no-build-cache
```

생성 결과: `build/dev121/synthetic-cache-benchmark.json`, `build/reports/tests/test/index.html`.
