# 개인 수강내역 캐시 정합성 재현

검증일: 2026-10-20 (Asia/Seoul)

## 결과

합성 학생과 실제 일회용 MySQL/Redis에서 두 가지 정합성 문제를 재현했다.
테스트 12건 통과, 실패/오류/건너뜀 0건이다. **불일치가 발생하는 현재 동작을 확인하는
진단 테스트이므로, 통과는 결함 재현 성공을 뜻한다. 수정 완료나 정상 동작 판정이 아니다.**

각 학생은 기존 3학점 과목 하나를 가지고 있고, 별도의 3학점 과목을 추가한다.
표의 캐시 학점은 캐시에 저장된 수강목록에서 합산한 값이다. 캐시가 학점 숫자나 졸업
판정 자체를 저장한다는 뜻은 아니다.

| 시나리오 | 횟수 | 종료 후 DB | 종료 후 캐시 | 계산 경계에서 읽은 학점 | 후속 일반 조회 |
| --- | ---: | ---: | ---: | ---: | ---: |
| 정상 순차 커밋 | 1 | 6 | 6 | 6 | — |
| 삭제 직후 다른 요청이 과거 목록을 재적재 | 5 | 6 | **3** | **3** | **3** |
| 새 목록을 캐싱한 뒤 강제 롤백 | 5 | 3 | **6** | 6 (롤백 전) | **6** |
| 캐시를 다시 채우기 전 강제 롤백 | 1 | 3 | 비어 있음 | 미실행 | 3 |

두 결함 시나리오 모두 마지막에 해당 합성 학생의 키만 삭제하면 다음 조회가 DB와
일치했다. DB 조회 자체의 오류가 아니라, Redis에 남은 수강목록이 후속 읽기에 영향을
주는 것을 함께 확인했다. 반복 5회는 고정된 실행 순서의 재현성 확인이며 운영 발생률이나
부하 테스트 결과가 아니다.

- [재현 테스트](../src/test/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/persistence/TakenLectureCacheConsistencyIntegrationTest.java)
- [실행 집계·관찰값·소스 해시](reviews/personal-cache-consistency-20261020.json)
- [후속 개선 계획](PERSONAL_TAKEN_LECTURE_CACHE_FIX_PLAN.md)

## 실제 코드와 테스트 대역의 경계

실제 운영 코드를 사용한 부분:

- `GenerateCustomizedTakenLectureService`와 그 Spring 트랜잭션 프록시.
- `FindTakenLectureService`와 그 `@Cacheable` 프록시.
- `TakenLectureCacheEvict`의 `@CacheEvict` 프록시.
- `RedisCacheConfig.cacheManager(...)`로 만든 Redis 캐시 관리자 및 JDK 직렬화.
- 수강내역 persistence adapter/repository/mapper와 실제 JPA 엔티티.
- MySQL의 실제 INSERT/SELECT/COMMIT/ROLLBACK과 Redis의 실제 저장/조회/삭제.

테스트 대역으로 한정한 부분:

- 사용자 조회는 새로 생성한 합성 학생 ID를 반환한다.
- 과목 조회는 직접 작성한 합성 3학점 과목을 반환한다.
- `GenerateOrModifyCompletedCreditUseCase` 경계는 실제 캐시 조회 서비스를 호출해
  수강내역 학점을 기록한 뒤, 지정된 위치에서 대기하거나 예외를 던지는 probe다.

따라서 **수강내역 캐시 불일치와 계산 입력 오염은 검증했지만, 전체 졸업요건 계산 및
`completed_credit` 저장 결과까지 검증한 것은 아니다.** 실제 HTTP·인증 경로,
수강내역 삭제/성적표 재업로드, Redis 장애·재시작, 다중 앱 인스턴스도 이번 범위 밖이다.

## 재현 순서

### 동시 조회

1. 기존 3학점 목록으로 개인 캐시를 채운다.
2. 별도 스레드에서 실제 과목 추가 서비스를 호출한다.
3. 새 과목 INSERT 후 캐시가 삭제된 지점에서 `CountDownLatch`로 쓰기 요청을 멈춘다.
4. 별도 DB 연결은 아직 커밋 전이라 기존 3학점만 조회되는지 확인한다.
5. 일반 조회 서비스를 호출해 기존 3학점 목록이 Redis에 다시 저장되게 한다.
6. 쓰기 요청을 재개한다. 계산 probe는 실제 캐시 조회를 통해 3학점을 읽는다.
7. 쓰기 요청 종료 후 DB 6학점 / 캐시 3학점 / 후속 조회 3학점을 대조한다.

임의의 sleep이나 경합 확률에 의존하지 않는다. 쓰기 요청의 트랜잭션 활성 상태,
실제 캐시 삭제, 별도 조회의 트랜잭션 분리, worker 종료도 확인한다.

### 롤백

1. 기존 3학점 목록으로 개인 캐시를 채운다.
2. 실제 과목 추가 서비스에서 새 과목을 INSERT하고 캐시를 삭제한다.
3. 계산 probe가 같은 트랜잭션에서 실제 조회 서비스를 호출한다.
4. 자기 트랜잭션의 변경을 포함한 6학점 목록이 커밋 전에 Redis에 저장되는지 확인한다.
5. probe에서 `RuntimeException`을 발생시켜 실제 쓰기 트랜잭션을 롤백한다.
6. DB 3학점 / 캐시 6학점 / 후속 조회 6학점을 대조한다.

## 격리 및 환경

- Java 21, MySQL 8.4.8, `REPEATABLE-READ`, Redis 7.4 계열의 로컬 이미지 사용.
- MySQL/Redis 이미지는 자동 다운로드하지 않는다. 정확한 이미지 ID는 증빙 JSON에 기록했다.
- 새 컨테이너 2개, `127.0.0.1`의 임시 포트, 새로운 `synthetic_cache_probe` DB만 사용한다.
- 컨테이너 재사용은 끄고 Redis 영속화도 끈다. 기존 컨테이너·볼륨·스냅샷을 연결하지 않는다.
- 일반 Spring Boot 앱을 기동하지 않는다. 필요한 빈만 별도 Spring 컨텍스트로 조립한다.
- `application*.yml`, Flyway, 웹 서버, 인증, AI 클라이언트, 외부 telemetry는 로드하지 않는다.
- 새 DB의 세 엔티티에만 Hibernate `create-only` DDL을 적용한다. 실제 DB 접속 대상과
  격리 수준을 확인하고, 개인 캐시 TTL이 1~600초 범위인지 Redis에서 읽어 확인한다.
- 학생과 과목은 테스트에서 직접 생성한다. 개인정보·덤프·기존 학사 데이터 조회는 없다.
- 실행 후 테스트 라벨의 MySQL/Redis 컨테이너 0개, 시작 전에 있던 실행 중 컨테이너
  4개의 ID 유지 여부를 확인했다. 데이터 내용의 전후 checksum 비교를 했다는 뜻은 아니다.

## 재실행

실행 플래그를 명시해야 하는 진단 테스트다. Docker가 없거나 이미지가 준비되지 않은
활성 실행은 실패하며, 실행하지 못한 검증을 성공으로 처리하지 않는다.
플래그를 `false`로 두고 존재하지 않는 Docker 소켓을 지정한 별도 실행에서는 4개
테스트 항목이 모두 건너뛰어졌고 컨텍스트 실행 출력도 없었다. 이 차단 검증은 위의
활성 실행 12건과 별도로 집계한다.

현재 macOS Docker Desktop 환경에서 사용한 명령에 재실행 강제 옵션을 붙인 예시:

```sh
RUN_TAKEN_LECTURE_CACHE_REPRODUCTION=true \
DOCKER_HOST=unix:///Users/jinhyeon/.docker/run/docker.sock \
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock \
TESTCONTAINERS_HOST_OVERRIDE=127.0.0.1 \
./gradlew test --tests '*TakenLectureCacheConsistencyIntegrationTest' \
  --offline --no-daemon --no-build-cache --rerun-tasks -x jacocoTestReport
```

다른 환경에서는 Docker 소켓 경로를 해당 환경에 맞춰야 한다. `--rerun-tasks`는
환경 변수만 바꿨을 때 Gradle이 이전 테스트 결과를 UP-TO-DATE로 재사용하지 않도록 한다.
Gradle XML/HTML 보고서는 이후 테스트 실행으로 덮어써질 수 있어, 위 JSON에 12건의
집계와 합성 관찰값을 별도로 보관했다.

초기 준비 중 세 번의 실행은 테스트 사례 실행 전에 실패했다. Docker Desktop의 Ryuk
소켓 마운트, 빈 DB에서 create-drop이 시도한 FK 삭제, 테스트 컨텍스트의 EntityManager
빈 선택 문제였다. 환경/테스트 조립을 수정한 다음 최종 12건을 실제 실행했다.

## 작업 상태와 다음 단계

- [x] 기존 DB를 사용하지 않는 실제 MySQL/Redis 재현 환경 구성.
- [x] 정상 커밋 및 캐시 재적재 전 롤백 대조군 확인.
- [x] 동시 조회 불일치 5회 재현.
- [x] 롤백 후 미커밋 목록 잔존 5회 재현.
- [x] 소스 해시·결과 보관 및 임시 컨테이너 정리 확인.
- [x] 명시적 실행 플래그가 없으면 재현 자원을 기동하지 않는 차단 확인.
- [x] 후속 계획의 방향을 개인 캐시 유지와 정합성·버전 관리·장애 대응 개선으로 수정.
- [ ] 데이터 revision 저장 위치와 조회 스냅샷 경계 상세 설계 및 구현.
- [ ] 수정 후 진단의 불일치 기대값을 정상 회귀 검증으로 전환.

이번 요청은 결함 확인까지다. 운영 코드·정책·배포 설정은 수정하지 않았고 커밋/푸시/배포도
하지 않았다. DEV-116의 정책·AI 구현 단계에는 변경을 추가하지 않았다.

후속 계획은 개인 캐시를 유지하면서 쓰기 중 캐시 우회, DB 데이터 revision에 결합한
키, 커밋된 스냅샷 게시, Redis 장애 시 DB fallback을 검증하는 방향이다. 캐시 형식 버전
전환과 학생 데이터 revision을 구분하며, 상세 설계·구현·추가 검증은 아직 수행하지 않았다.
