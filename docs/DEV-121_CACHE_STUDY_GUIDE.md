# DEV-121 사용 기술과 선택 이유 — 공부용 가이드

후속 이수학점 최적화: [중복 조회 원인과 개선](DEV-121_COMPLETED_CREDIT_PERFORMANCE.md). 합성 NORMAL 조건의 최신 SQL은 off 9회 / shadow 9회 / on 8회다.
확대 예열의 이수학점 p95는 on이 off보다 9/9개 조건에서 5.3~21.9% 낮았으며, 짧은 예열의 일부 악화 결과도 보존한다.
최신 전체 회귀는 834건 중 830건 통과·실패/오류 0·선택 실행 제외 4건이다.

대상: 개인 수강내역 캐시. 기준: 2026-10-27의 구현. 현재 브랜치: `fix/DEV-121` (2026-10-29 이름 변경).

이 문서는 성과 수치보다 **문제 → 기술 → 선택 이유 → 코드 → 한계**를 설명한다.
설계와 실험 결과는 [결과 보고서](DEV-121_CACHE_OPTIMIZATION_RESULT.md)에 따로 보관했다.
2026-10-29의 번호 정리·운영 환경변수와 적용 순서는 [배포 준비](DEV-121_CACHE_DEPLOYMENT.md)를 따른다.
같은 날 추가한 [실제 HTTP API 비교](DEV-121_CACHE_HTTP_COMPARISON.md)는 로그인/JWT를 포함하며,
수강내역은 개선됐지만 전체 이수학점은 일부 조건에서 p95가 악화한 결과까지 보관한다.

## 1. 먼저 알아둘 세 단어

- **정합성**: 여기서는 수강내역과 선택한 캐시 버전이 서로 맞는다는 뜻이다.
  변경 성공 후 새 트랜잭션으로 시작한 조회는 커밋된 버전을 선택한다.
  변경과 겹쳐 이미 시작된 읽기는 이전의 일관된 DB 스냅샷을 볼 수 있다.
- **revision**: 데이터가 바뀔 때 증가하는 번호다. `takenRevision`은 학생별 목록,
  `catalogRevision`은 공통 과목 정보의 변경을 표시한다.
- **snapshot**: 특정 버전에 해당하는 값의 묶음이다. 이후 계산 중에 캐시 안의 원본이 변하지 않게 구성한다.

## 2. 기술 지도

| 기술/패턴 | 해결하려는 문제 | 이번 적용 |
| --- | --- | --- |
| Spring 트랜잭션 | 목록만 저장되고 버전/요약은 실패하는 부분 성공 | 목록·revision·요약을 같은 트랜잭션에 참여 |
| InnoDB MVCC / REPEATABLE READ | 버전은 이전 값, 목록은 새 값인 혼합 조회 | 같은 읽기 스냅샷에서 버전과 목록 선택 |
| JPA 비관적 락 / FOR UPDATE | 같은 학생의 변경·재계산이 동시에 섞임 | 첫 스냅샷 읽기 전에 사용자 행 잠금 |
| 원자 증가 + 버전 캐시 키 | 과거 reader가 늦게 과거 값을 다시 저장 | 데이터 revision별 키 분리 |
| MySQL 트리거 | 직접 SQL로 과목이 변경되어도 캐시는 예전 정보 | 과목 DML과 함께 catalogRevision 증가 |
| TransactionSynchronization.afterCommit | 롤백될 DB 값이 공유 캐시에 공개됨 | 성공한 읽기 트랜잭션 뒤 신규 게시 |
| Cache-aside / Redis / 메모리 LRU | 반복 목록 조회와 Redis 왕복·JSON 복원 비용 | DB 버전 확인 후 local → Redis → DB |
| 조회 DTO와 scalar projection | 사용자·두 버전을 따로 읽는 SQL 비용, 오래된 ORM 필드 | 필요한 묶음을 한 SELECT로 반환 |
| Java record / Jackson | 캐시 객체 변형, 불필요한 사용자 정보 직렬화 | 불변 스냅샷을 JSON으로 저장 |
| Timeout / Semaphore / 장애 우회 | 느린 Redis에 요청이 묶이거나 반복 실패 | 200ms·16개 제한·5초 우회·복구 probe |
| Shadow / 모드 전환 | 새 경로의 오류가 바로 사용자에게 노출됨 | DB 반환·캐시 비교 후 on 전환 |
| Testcontainers / latch / Micrometer | 동시성 결함을 재현하기 어렵고 성능 원인이 불명확 | 실제 DB/Redis와 결정적 실행 순서·지표 |

## 3. Spring 트랜잭션 — 여러 DB 변경을 하나로 묶기

**문제**: 수강목록은 추가됐지만 revision 증가나 학점 요약 저장이 실패하면 서로 다른 상태가 남을 수 있다.

**기술**: Spring `@Transactional`. 내부 호출은 기존 쓰기 트랜잭션에 참여하고,
revision 변경 persistence 메서드는 `Propagation.MANDATORY`로 기존 트랜잭션을 요구한다.

**선택 이유**: DB 목록·버전·요약은 함께 커밋하거나 함께 롤백해야 한다.
계산 때문에 새 트랜잭션을 열면 아직 커밋하지 않은 자기 변경을 다른 연결에서 볼 수 없으므로 기존 트랜잭션을 사용한다.

**이번 코드**: [수강 추가 서비스](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/application/service/save/GenerateCustomizedTakenLectureService.java),
[persistence adapter](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/persistence/TakenLecturePersistenceAdapter.java).

**한계**: 이 DB 트랜잭션에 Redis가 자동으로 포함되는 것은 아니다. DB 롤백이 Redis의 SET을 자동 취소하지 않는다.
또한 안쪽 메서드의 readOnly/isolation 선언만 보고 실제 트랜잭션을 판단하면 안 된다.
기본 전파에서 기존 트랜잭션에 참여하면 바깥 설정을 따르므로 캐시 adapter에서 현재 상태를 검사한다.
[Spring 트랜잭션 전파 공식 문서](https://docs.spring.io/spring-framework/reference/data-access/transaction/declarative/tx-propagation.html).

**직접 확인할 것**: 추가 서비스 → 목록 저장 → 학점 계산/요약 저장이 같은 트랜잭션으로 이어지는 위치.

## 4. REPEATABLE READ / MVCC — 버전과 목록의 시점을 맞추기

**문제**: 먼저 revision=7을 읽은 뒤 다른 요청이 revision=8과 새 목록을 커밋하고,
이후 조회가 새 목록을 읽으면 ‘새 목록을 버전 7에 저장’할 수 있다.

**기술**: InnoDB의 일관된 스냅샷 읽기와 `Isolation.REPEATABLE_READ`.
일반 consistent read는 같은 트랜잭션에서 첫 스냅샷 기준을 공유하고, 자기 트랜잭션의 변경은 볼 수 있다.
[MySQL consistent read 공식 문서](https://dev.mysql.com/doc/refman/8.4/en/innodb-consistent-read.html).

**선택 이유**: revision과 cache miss 시 목록을 같은 DB 스냅샷에서 읽어야 키와 값이 맞는다.
캐시 키에 버전을 추가하는 것만으로 이 시점 혼합까지 해결되지는 않는다.

**이번 코드**: [조회 서비스](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/application/service/find/FindTakenLectureService.java),
[캐시 사용 경계](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/CachedTakenLectureAdapter.java)의 `isSnapshotRead()`.
명시된 읽기 전용 RR/SERIALIZABLE만 캐시를 허용하며 쓰기·RC·비트랜잭션·DEFAULT는 DB 경로를 사용한다.

**한계**: RR이 모든 진행 중 요청을 즉시 최신 커밋으로 바꾸지는 않는다. 쓰기 요청을 자동 직렬화하는 기능도 아니다.
수동으로 물리 연결의 격리 수준을 바꾸는 경로는 현재 계약 밖이다.

**실제 API에서 찾은 누락**: 상세 조회의 바깥 `CalculateSingleDetailGraduationService`는 readOnly만 선언해서
안쪽 RR 선언이 적용되지 않았고 캐시가 안전하게 우회됐다. HTTP 테스트에서 이를 확인하고 바깥 경계에도 RR을 명시했다.
DB의 기본 격리 수준이 RR이라는 사실과 Spring 트랜잭션에 RR이 명시됐다는 사실은 구분해야 한다.

## 5. 비관적 락 — 같은 학생의 변경과 재계산 순서를 정하기

**문제**: 현재 3학점인 학생에게 A와 B가 각각 3학점 과목을 동시에 추가하면,
재계산이 서로의 변경을 빠뜨려 최종 요약을 6학점으로 저장할 수 있다.

**기술**: JPA `@Lock(PESSIMISTIC_WRITE)`와 DB locking read. 사용자 행을 먼저 잠근다.

**선택 이유**: 목록이 비어 있어도 사용자 행은 존재하므로 같은 학생의 변경 진입점을 하나로 모을 수 있다.
DB 행 잠금은 같은 DB에 연결된 여러 앱 인스턴스에도 적용된다. 첫 snapshot 조회 전에 잠그는 순서가 중요하다.
[MySQL locking read 공식 문서](https://dev.mysql.com/doc/refman/8.4/en/innodb-locking-reads.html).

**이번 코드**: [UserRepository.findByIdForUpdate](../src/main/java/com/plzgraduate/myongjigraduatebe/user/infrastructure/adapter/persistence/repository/UserRepository.java),
서비스 진입의 `lockForChange()`.

**한계**: 같은 학생의 쓰기는 대기하므로 트랜잭션이 길면 지연도 늘어난다.
별도 학생의 개인 수강 쓰기를 모두 하나의 전역 락으로 묶은 것은 아니다.
이 작업의 락은 DB 행 잠금이며 Redis 분산 락/Redisson을 도입한 것은 아니다.

## 6. 원자 증가와 버전 키 — 늦게 도착한 과거 값 격리

**문제**: 캐시를 지워도 이미 DB의 과거 목록을 읽은 reader가 나중에 같은 키를 다시 채울 수 있다.

**기술**: DB의 원자적 revision 증가와 버전별 key. 설명용 SQL은 다음과 같다. 실제 repository 호출은 JPQL이다.

```sql
UPDATE `user`
SET taken_lecture_revision = taken_lecture_revision + 1
WHERE id = :userId;
```

```text
reader A: 수강 버전 7, 과목 버전 12의 3학점을 읽음
writer B: 6학점과 수강 버전 8을 함께 커밋
reader A: 뒤늦게 ...:7:12에 3학점 저장
새 reader: DB에서 8/12 확인 → ...:8:12 선택
```

**선택 이유**: 정확성을 ‘과거 키 삭제가 꼭 성공했는가’에 의존하지 않게 한다.
원자 증가는 데이터 세대를 표시하고, 비관적 락은 변경/계산 순서를 조정한다. 두 역할이 다르다.

**키 해석**: `takenLectures:v3:prod-a:42:8:12`

| 조각 | 의미 |
| --- | --- |
| v3 | 직렬화·캐시 계약의 형식 버전 |
| prod-a | 환경/전환 namespace |
| 42 | 사용자 ID |
| 8 | 학생 수강목록 버전 |
| 12 | 공통 과목 정보 버전 |

**이번 코드**: [UserRepository.advanceTakenLectureRevision](../src/main/java/com/plzgraduate/myongjigraduatebe/user/infrastructure/adapter/persistence/repository/UserRepository.java),
[TakenLectureCache.key](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureCache.java).
이 revision은 애플리케이션이 직접 증가시키는 값이며 JPA의 `@Version` 낙관적 락 필드와 구분한다.

**한계**: 모든 학생 writer가 같은 트랜잭션에서 revision을 갱신해야 한다.
TTL은 과거 버전 키 정리용이다. TTL 10분 자체가 최신성을 보장하는 것은 아니다.

## 7. MySQL 트리거 — 과목 정보도 버전에 포함하기

**문제**: 학생 수강목록이 같아도 과목 학점이 3→4로 바뀌면 계산이 달라진다.
학생 revision만 확인하면서 과목 정보를 캐싱하면 이 변경을 놓친다.

**기술**: `lecture`의 INSERT/UPDATE/DELETE 뒤 `lecture_catalog_revision`을 증가시키는 트리거 3개.
트랜잭션 테이블에서 과목 수정과 버전 증가가 함께 커밋/롤백된다.

**선택 이유**: 과목 변경이 앱 밖의 정상 SQL 적재로 발생해도 버전을 올려야 하기 때문이다.
과목 정보를 매 요청 DB에서 읽던 v2의 비용을 줄이면서 데이터 변경을 추적한다.

**이번 코드**: [V15 마이그레이션](../src/main/resources/db/migration/V15__version_lecture_catalog.sql).
[MySQL 트리거 공식 문서](https://dev.mysql.com/doc/refman/8.4/en/create-trigger.html).

**한계**: MySQL 전용 구현이고 생성 권한/DEFINER 관리가 필요하다.
TRUNCATE·테이블 교체·복구는 일반 DML 경로와 다르므로 off/새 namespace 전환 절차가 필요하다.
한 과목 변경도 전체 catalogRevision을 바꾸므로 많은 사용자 키가 cold 상태가 될 수 있다.

## 8. afterCommit — 롤백될 값을 캐시에 공개하지 않기

**문제**: DB에서 자기 미커밋 6학점을 계산해 캐시에 저장한 뒤 롤백하면,
DB는 3학점인데 Redis에는 6학점이 남을 수 있다.

**기술**: `TransactionSynchronization`의 `afterCommit()`과 쓰기 트랜잭션의 캐시 우회.

**선택 이유**: 쓰기 중 계산은 DB에서 자기 변경을 직접 읽고, 신규 공유 캐시 적재는
성공한 읽기 트랜잭션 뒤에만 수행한다. 새 DB snapshot의 메모리 적재도 게시 성공 뒤에 수행한다.

**이번 코드**: [TakenLectureCache.publishAfterCommit](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureCache.java).

**한계**: 커밋 뒤 프로세스가 종료되거나 Redis 저장이 실패하면 캐시가 비어 있을 수 있다.
다음 조회가 DB 버전을 확인하고 DB에서 채우므로 복구할 수 있다.
afterCommit만으로 늦은 과거 reader 문제를 해결하지는 않는다. 그 문제에는 버전 키가 함께 필요하다.

## 9. Cache-aside / Redis / 메모리 LRU — 반복 조회 비용 줄이기

**문제**: 매번 목록/과목을 DB에서 읽으면 반복 비용이 든다. Redis hit도 네트워크·JSON 복원 비용은 남는다.

**기술**: 애플리케이션이 miss 시 DB를 읽어 적재하는 cache-aside와 두 단계 캐시.
[Redis cache-aside 공식 문서](https://redis.io/docs/latest/develop/use-cases/cache-aside/).

```text
DB에서 현재 사용자 + 수강 버전 + 과목 버전 조회
→ 해당 버전의 메모리 캐시 확인
→ 없으면 Redis
→ 없거나 접근 실패하면 DB 목록 조회
→ 성공한 읽기 커밋 후 게시
```

**선택 이유**: DB 조회를 완전히 없애는 대신 작은 버전 확인은 남기고,
목록 읽기·Redis 왕복·JSON 복원 같은 반복 비용을 줄인다.

**이번 코드**: [TakenLectureCache.localGet/localPut](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureCache.java).
`LinkedHashMap`의 access-order로 LRU를 구성하고 synchronized로 맵 접근을 보호한다.
프로세스당 128개 버전 키, TTL 10분이며 `local-entries=0`으로 메모리 계층을 끌 수 있다.
[Java LinkedHashMap 공식 문서](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/LinkedHashMap.html).

**한계**: 최근 사용한 키를 남기는 LRU와 시간으로 만료하는 TTL은 다른 역할이다.
128은 키 개수 제한이며 바이트 제한이 아니다. 메모리 cache는 프로세스별로 따로 있다.
매번 DB revision을 확인하므로 다른 앱의 변경도 새 버전 key를 선택해 반영한다.

## 10. 조회 DTO / scalar / 불변 JSON — 왕복과 객체 부작용 줄이기

**문제**: 사용자·수강 버전·과목 버전을 각각 조회하면 왕복이 늘어난다.
ORM이 이미 관리 중인 엔티티의 revision은 bulk UPDATE 뒤 오래된 값일 수도 있다.
캐시에 도메인 객체 전체를 넣으면 사용자 정보와 계산 중 상태까지 함께 보관할 위험이 있다.

**기술**: 한 쿼리의 조회용 record, 별도 scalar revision, `Java record` 스냅샷과 Jackson JSON.

**이번 코드**: [UserRepository.findTakenLectureReadContext](../src/main/java/com/plzgraduate/myongjigraduatebe/user/infrastructure/adapter/persistence/repository/UserRepository.java)는
사용자 엔티티와 두 scalar 버전을 함께 조회해 `TakenLectureReadContextRow`로 묶는다.
기존 managed 엔티티의 revision 필드 대신 SELECT에서 얻은 scalar 값을 사용한다.
[TakenLectureSnapshot](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureSnapshot.java)은
수강 항목과 과목 정보만 저장하고, 조회 때 현재 사용자와 새 도메인 객체를 만든다.

**선택 이유**: 데이터 조회 SQL을 1회로 줄이고, 오래된 ORM 필드·캐시 객체 변형·사용자 정보 포함을 피한다.
`record`만으로 깊은 불변성이 자동 보장되는 것은 아니므로 entries에 `List.copyOf()`를 쓰고 항목도 record로 구성했다.

**추가 발견**: JDBC `getTransactionIsolation()`도 SELECT를 보낼 수 있었다.
현재는 Spring 트랜잭션 메타데이터를 사용하며 서버 Com_select로 데이터 조회 1회를 확인한다.
SQL 숫자는 트랜잭션 시작/설정/종료 명령까지 모두 합한 숫자는 아니다.
실제 HTTP 측정에서는 Spring의 트랜잭션 준비가 JDBC 격리 수준을 조회하는 SELECT도 포함돼,
수강내역의 서버 Com_select가 off 3회 / on 2회였다. 위의 트랜잭션 내부 2회 / 1회와 측정 경계가 다르다.

## 11. 장애 대응 — 기다리는 시간·동시 개수·재시도 빈도 나누기

| 수단 | 무엇을 제한하는가 | 이번 적용 |
| --- | --- | --- |
| Timeout | 한 Redis 접근을 기다리는 시간 | Lettuce 연결/명령 200ms |
| Bulkhead | 동시에 Redis에 접근하는 개수 | Semaphore 16 permit, tryAcquire 실패 시 DB |
| 장애 우회/복구 probe | 고장난 Redis에 반복 접근하는 빈도 | 실패 후 5초 우회, 이후 한 복구 probe |
| Fallback | 캐시 실패 시 대신 수행할 작업 | DB 조회 |

**선택 이유**: timeout만 두면 많은 요청이 동시에 timeout을 기다릴 수 있고,
동시성 제한만 두면 허용된 요청이 오래 걸릴 수 있다. 제어 대상이 달라 함께 사용한다.

**이번 코드**: [TakenLectureRedisStore](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureRedisStore.java),
[TakenLectureCache.tryRedis](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/cache/TakenLectureCache.java).
Semaphore·AtomicLong·AtomicBoolean으로 만든 작은 제어 로직이다. Resilience4j를 도입한 구현은 아니다.
[Java Semaphore 공식 문서](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html).

**한계**: 16은 프로세스별 개인 캐시 Redis 호출 한도다. 전체 서버군/DB 요청 수 제한이 아니다.
DB fallback이 몰리면 DB도 느려질 수 있다. 인증 Redis나 AI 상담의 장애 격리까지 보장하지 않는다.
DB 버전을 확인할 수 없으면 오래된 메모리 값을 최신으로 반환하지 않는다.

## 12. Shadow / 테스트 / 계측 — 맞는지와 빠른지를 별도로 확인

**문제**: 새 캐시가 빠르더라도 계산이 틀릴 수 있고, 합계만 같아도 과목이 다를 수 있다.
동시 요청을 단순 반복하면 문제가 되는 실행 순서를 놓치기 쉽다.

**기술과 적용**:

- off/shadow/on: shadow는 DB를 반환하면서 캐시 snapshot을 비교한다. 학생의 쓰기를 두 번 실행하지 않는다.
- Testcontainers + JUnit: 일회용 실제 MySQL/Redis와 Spring 트랜잭션 프록시를 사용한다.
- CountDownLatch: reader를 원하는 지점에서 멈추고 writer를 커밋시킨 뒤 reader를 재개한다.
- Fault injection: Redis 지연/재시작, 게시 예외, 롤백, 커밋 뒤 별도 JVM 종료를 주입한다.
- Micrometer/Hibernate/Hikari/Com_select: hit/miss/error, SQL, 풀 대기, 지연을 구분해 확인한다.

**선택 이유**: 순서와 장애를 통제해 재현하고, DB의 같은 버전을 정답으로 전체 필드를 비교하기 위해서다.
mock만으로 실제 MySQL 격리/락/롤백이나 Redis 재연결 동작을 입증할 수는 없다.

**한계**: 현재 실험은 직접 만든 합성 학생·과목과 고정 정책 포트를 사용한다.
전체 운영 정책/HTTP/auth/실제 운영 용량 검증은 별도다. ArchUnit·AI 상담 장애 격리도 별도 후속 주제다.
이는 10월 27일 서비스 실험의 범위다. 후속 HTTP 실험은 실제 로그인/JWT와 5종 API를 검증했지만,
단일 합성 학번·전공이며 운영 용량·학사정책 전체·장애 중 로그인/refresh는 여전히 검증 범위 밖이다.

## 13. 코드를 읽을 순서와 스스로 확인할 질문

1. 추가 서비스와 `@Transactional`: DB에 무엇을 함께 저장하고 롤백하는지 표시한다.
2. `lockForChange()`와 repository: 잠금을 첫 조회 전에 잡는 이유를 설명한다.
3. revision 증가와 캐시 key: 오래된 reader가 늦게 게시해도 새 요청이 왜 선택하지 않는지 추적한다.
4. `isSnapshotRead()`/`publishAfterCommit()`: 쓰기 중 캐시 우회와 커밋 후 게시가 각각 막는 문제를 구분한다.
5. `findReadContext()`와 `TakenLectureSnapshot`: DB에 남긴 조회와 캐시에서 복원한 데이터를 구분한다.
6. `localGet()`/`tryRedis()`: LRU·TTL·timeout·bulkhead·장애 우회의 제어 대상을 구분한다.
7. 통합 테스트의 latch: 특정 버전과 목록이 어떤 순서로 읽히는지 종이에 써본다.

스스로 설명할 수 있으면 좋은 질문:

- DB 트랜잭션이 있는데도 Redis에 잘못된 값이 남을 수 있는 이유는 무엇인가?
- 락이 있는데 revision도 필요한 이유, revision이 있는데 RR도 필요한 이유는 무엇인가?
- afterCommit만 추가해도 늦은 과거 값 게시가 해결되는가?
- TTL 10분과 ‘최신 버전만 선택’은 어떻게 다른가?
- 메모리에 값이 있어도 DB를 한 번 확인하는 이유는 무엇인가?
- SQL 1회라도 트랜잭션 제어와 네트워크 비용이 모두 사라지는 것은 아닌 이유는 무엇인가?


## 14. 이미 읽은 데이터 재사용 — 정합성과 성능을 함께 지키기

전체 이수학점 API에는 두 종류의 불필요한 조회가 남아 있었다. 사용자를 먼저 읽은 뒤 캐시 경로에서
사용자와 revision을 다시 읽었고, 새 계산 결과를 반환하면서 저장된 학점 요약도 읽었다.
캐시가 수강 목록 JOIN을 줄인 효과가 재조회 비용으로 상쇄돼 off/on 모두 실제 SELECT가 10회였다.

Java record인 `TakenLectureRead`로 현재 DB 사용자와 요청 전용 수강 inventory를 묶어 넘겼다.
계산 서비스는 전달받은 inventory를 사용한다. 조회 응답에는 저장 행 ID가 없으므로 기존 요약 조회를 제거했다.
쓰기에서는 기존 ID가 필요하기 때문에 그대로 읽는다. 그 결과 off는 9회, on은 8회로 줄었다.

핵심은 revision 검사를 없애는 것이 아니라 그 검사에서 이미 얻은 사용자 정보를 재사용하는 것이다.
RR 안에서 현재 DB 버전을 확인하고, 같은 스냅샷의 사용자와 과목을 조합하는 규칙은 그대로다.
공유 캐시에 User를 저장한 것도 아니다. 사용자 정보 변경은 다음 요청의 DB 조회에 반영된다.

실제 요청의 SQL은 MySQL Performance Schema의 정규화된 digest와 서버 SELECT 카운터를 함께 확인했다.
repository 메서드 호출 수만 세면 JDBC가 격리 수준을 확인하는 SELECT를 놓칠 수 있다.
자세한 코드 흐름과 수정 전후 JAR 비교는 [후속 보고서](DEV-121_COMPLETED_CREDIT_PERFORMANCE.md)에 정리했다.
