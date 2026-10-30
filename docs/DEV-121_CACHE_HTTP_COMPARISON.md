# DEV-121 실제 HTTP API의 off / shadow / on 비교

후속 이수학점 최적화: [중복 조회 원인과 개선](DEV-121_COMPLETED_CREDIT_PERFORMANCE.md). 아래는 중복 조회 제거 전 실험 기록이며, 당시 숫자를 보존한다.

실행일: 2026-10-29 / 브랜치: `fix/DEV-121`

실제 애플리케이션 JAR와 별도 합성 MySQL·Redis로 검증했다. 성능 측정 GET **38,880건**은 모두 HTTP 200이며
off의 DB 기준 응답과 일치했다. 수강내역은 on에서 9/9개 조건의 p95가 개선됐지만,
전체 이수학점은 4/9개 조건만 개선돼 모든 API가 빨라졌다고 판단하지 않는다.

운영 DB·원본 스냅샷·운영 자격증명은 사용하지 않았고 커밋·푸시·운영 배포는 하지 않았다.

## 환경과 범위

| 항목 | 사용한 구성 |
| --- | --- |
| DB / 캐시 | 새 일회용 MySQL 8.4.8 / Redis 7.4.11, 이미지 ID 기록 |
| 앱 | 실제 bootJar, prod 프로파일, 별도 JVM 12회 시작·종료 |
| Java / JVM | Java 21.0.6, `-Xms64m -Xmx256m -XX:ActiveProcessorCount=2` |
| 연결 풀 / 스키마 | Hikari 최대 10, 앱 DDL none, 앱 Flyway validate·적용 이력 확인 |
| 개인 캐시 | 200ms, TTL 10분, 5초 장애 우회, 동시 Redis 16개, L1 최대 128개 키 |
| 공유 마스터 캐시 | 세 모드 모두 off로 고정해 개인 캐시 효과를 비교 |
| 인증 / HTTP | 실제 로그인·BCrypt·JWT 필터·컨트롤러·서비스·JPA·JSON 응답 |
| 주소 / 계정 | loopback HTTP·DB·Redis, 사전 마이그레이션 후 앱은 DML 계정 사용 |
| 데이터 | 직접 만든 학생 14명, 합성 과목 182개와 공통·핵심·학문기초·전공 매핑 |

성능 학생은 12명(30/60/120과목 × 각 4명), 변경·장애용 학생은 2명이다.
NORMAL / 20학번 / 영어영문학전공의 합성 조건만 사용했다. 실제 과목·정책 데이터를 복사하지 않았다.
합성 매핑은 API 비교용이며 실제 졸업정책의 정답을 인증하는 fixture는 아니다.

초기 빈 DB에서 전체 엔티티 스키마를 만든 뒤, 캐시 컬럼/테이블을 제거하고 Flyway V14/V15를 실제 적용했다.
baseline 13은 이 합성 fixture 전용이다. 운영 baseline 변경이나 DEV-116 정책 마이그레이션 검증을 의미하지 않는다.
앱 시작 때 자동 DDL은 none이며, privileged migration과 DML 앱 계정을 분리했다.

빌드한 JAR를 그대로 실행하며 테스트용 서비스·정책 포트·인증 대역·MockMvc를 넣지 않았다.
앱 JVM에는 JaCoCo agent가 없다. Sentry·SQL 콘솔 로그와 자격증명 출력 로거를 끄고 상속 환경변수를 제거했다.
생성한 자격증명 설정 파일은 프로세스 종료 후 삭제한다.

운영과 DB/Redis 계열·드라이버·JVM 옵션·캐시 설정을 맞춘 로컬 검증이다.
실제 운영의 패치 버전, TLS/프록시, 자원 제한, 네트워크 지연, 트래픽을 확인하거나 복제한 것은 아니다.
Redis 영속 저장은 일회용 테스트에서 껐다. `ActiveProcessorCount=2`는 실제 CPU cgroup 제한과 같지 않다.

## HTTP 검증에서 찾고 수정한 것

`CalculateSingleDetailGraduationService`의 바깥 트랜잭션은 기존에 `readOnly=true`만 선언했다.
내부 `FindTakenLectureService`의 RR 선언은 이미 열린 트랜잭션에 참여할 때 격리 수준을 바꾸지 못했다.
따라서 안전한 스냅샷 경계만 허용하는 캐시 adapter가 졸업 상세 API에서 shadow/on을 우회했다.

최초 HTTP probe에서 상세 API의 `shadow=0`, `transaction_bypass=1`을 확인했고,
바깥 서비스에도 **명시적인 REPEATABLE_READ**를 적용했다. 수정 후 5종 API 모두 shadow 비교와 on hit가 확인됐다.
졸업 계산 알고리즘과 정책은 변경하지 않았다.

[초기 캐시 우회 증거](reviews/dev121-http-initial-finding-20261029.json)를 별도로 보존했다.
초기 관측 연결의 초기화 SELECT가 섞인 SQL 숫자는 사용하지 않았다. 최종 관측 풀은 미리 예열하고,
상태 조회 자체의 Com_select 증가가 0임을 assertion으로 확인했다.

## 비교 방법

- 대상: 수강내역, 전체 이수학점, 공통교양 상세, 핵심교양 상세, 전공필수 상세 GET 5종.
- 각 모드에서 같은 학생·DB·정책·엔드포인트·요청 수를 사용했다.
- 매 앱에서 로그인 후 예열하고 30/60/120과목, 동시성 1/4/8, 각 조건 96요청을 실행했다.
- 모드 순서는 `off/shadow/on`, `shadow/on/off`, `on/off/shadow`로 회전해 3회 반복했다.
- 405개 집계 × 96 = 38,880건. 로그인·사전 probe·예열·변경·장애 검증을 포함한 API 호출은 42,442건이다.
- 모든 필드를 비교한다. 수강목록의 정렬 순서는 그대로 검사하고, 학점/상세의 집합형 컬렉션 순서만 정규화했다.
- SQL은 격리된 MySQL 서버의 Com_select 증가를 센다. 모든 측정 GET의 INSERT/UPDATE/DELETE는 0이다.
- on의 모든 측정 배치에서 96요청 모두 local_hit, shadow의 모든 배치에서 96요청 모두 DB 비교를 확인했다.

아래 값은 조건별 **3회 p95의 중앙값**이다. 여러 조건의 요청을 합쳐 다시 구한 p95가 아니다.
표는 RR 경계를 보완한 동일 코드의 모드 비교이며, 변경 전 운영 바이너리와의 배포 비교가 아니다.

## 성능 결과

동시성 4, 단위 ms. 변화율은 반올림 전 수치의 `(on / off - 1) × 100`이다. 음수는 개선이다.

| API | 과목 수 | off | shadow | on | on p95 변화 |
| --- | ---: | ---: | ---: | ---: | ---: |
| 수강내역 | 30 | 8.49 | 10.91 | 6.58 | -22.5% |
| 수강내역 | 60 | 6.82 | 16.50 | 5.25 | -23.1% |
| 수강내역 | 120 | 7.77 | 13.75 | 5.11 | -34.2% |
| 전체 이수학점 | 30 | 18.31 | 18.12 | 18.33 | +0.1% |
| 전체 이수학점 | 60 | 17.78 | 15.34 | 15.30 | -13.9% |
| 전체 이수학점 | 120 | 13.04 | 15.96 | 15.29 | **+17.3%** |
| 공통교양 상세 | 30 | 7.69 | 12.53 | 6.85 | -10.9% |
| 공통교양 상세 | 60 | 7.33 | 10.46 | 7.48 | +2.0% |
| 공통교양 상세 | 120 | 7.99 | 13.49 | 7.21 | -9.7% |
| 핵심교양 상세 | 30 | 8.11 | 12.39 | 7.31 | -9.8% |
| 핵심교양 상세 | 60 | 8.58 | 11.39 | 7.01 | -18.3% |
| 핵심교양 상세 | 120 | 9.60 | 12.08 | 7.03 | -26.8% |
| 전공필수 상세 | 30 | 9.44 | 14.02 | 8.50 | -10.0% |
| 전공필수 상세 | 60 | 9.43 | 15.89 | 8.71 | -7.6% |
| 전공필수 상세 | 120 | 12.73 | 11.49 | 7.84 | -38.4% |

전체 동시성 1/4/8을 포함한 9개 조건씩 비교하면 다음과 같다.

| API | on이 더 빠른 조건 | on p95 변화 범위 |
| --- | ---: | ---: |
| 수강내역 | 9/9 | -50.0% ~ -7.7% |
| 전체 이수학점 | 4/9 | -34.4% ~ **+18.7%** |
| 공통교양 상세 | 7/9 | -27.3% ~ +9.5% |
| 핵심교양 상세 | 8/9 | -39.7% ~ +14.5% |
| 전공필수 상세 | 9/9 | -44.3% ~ -0.5% |

전체 45개 조건 중 37개에서 p95가 개선됐다. 가장 큰 악화는 30과목·동시성 8의 전체 이수학점으로
off 23.22ms → on 27.56ms(+18.7%)다. 20% 이상 악화한 조건은 없지만, 이를 운영 허용 기준으로 정한 것은 아니다.
전체 측정의 최대 개별 지연은 145.43ms다. 반복별 편차를 함께 보관하며 개선/악화 원인을 GC나 CPU로 단정하지 않는다.
별도 앱 JVM의 CPU 샘플은 이 호스트에서 유효하게 얻지 못해 분석에서 제외했다. 원시 JSON의 CPU 0은 측정값으로 해석하지 않는다.

## 실제 API의 SELECT 수

| API | off | shadow | on |
| --- | ---: | ---: | ---: |
| 수강내역 | 3 | 3 | 2 |
| 전체 이수학점 | 10 | 11 | 10 |
| 공통교양 상세 | 4 | 5 | 4 |
| 핵심교양 상세 | 4 | 5 | 4 |
| 전공필수 상세 | 5 | 6 | 5 |

이 숫자는 HTTP 요청 전체에서 서버가 처리한 SELECT 수다. 이전 서비스 실험의 ‘캐시 hit 1회’는
이미 시작된 트랜잭션 내부의 데이터 조회만 센 값이었다.
현재 Spring JDBC 6.1.13의 트랜잭션 준비는 명시된 격리 수준을 확인하며 Connection.getTransactionIsolation()을 호출하고,
Connector/J 설정상 이 호출도 SELECT를 보낸다. 실제 라이브러리 bytecode와 서버 계측으로 확인했다.
SET/autocommit/COMMIT 등 모든 프로토콜 명령까지 위 숫자에 포함한 것은 아니다.

이수학점·상세 API는 바깥에서 사용자를 먼저 읽는다. on에서는 버전 확인 쿼리도 실행하므로
목록 쿼리 생략과 상쇄돼 API 전체 SELECT 수가 줄지 않았다. 남은 정책 조회·계산·직렬화 비용도 존재한다.
개인 캐시만으로 전체 이수학점의 p95 개선까지 일관되게 얻지는 못했다.
shadow는 DB 목록과 캐시 양쪽을 확인하므로 비교 비용이 있다. 성능 모드로 해석하지 않는다.

## 변경·롤백·장애 결과

각 모드에서 다음 세 검증 묶음을 실행해 9/9개 묶음이 통과했다.

| 묶음 | 실제 실행 내용 |
| --- | --- |
| 인증·변경·롤백·카탈로그 | 로그인/JWT, 무인증 거부, 과목 추가 3→6학점·삭제 6→3학점, revision 증가, 저장 중 강제 실패·롤백, 과목 학점 변경 반영 |
| 동시 조회 | HTTP reader 4개 × 32회와 writer의 추가/삭제 12회. 겹친 조회는 일관된 3/6학점, 변경 응답 뒤 조회는 해당 커밋 상태 |
| Redis 지연·복구 | Redis pause 중 L1에 없는 학생의 조회 6회, 5.5초 뒤 재조회와 캐시 복구 |

롤백은 completed_credit INSERT/UPDATE 직전 합성 DB 트리거로 실패시켰다.
수강 추가·revision·사용자 요약·학점 요약이 모두 원상태이고 후속 API도 동일한지 검사했다.
Redis 장애에서 관측한 값은 다음과 같다.

| 모드 | Redis 지연 중 첫 API | 이후 5회 | 결과 |
| --- | ---: | ---: | --- |
| off | 6.07ms | 2.37~7.42ms | DB 경로, 개인 캐시 Redis 접근 없음 |
| shadow | 215.03ms | 6.50~8.14ms | 첫 timeout 뒤 DB 우회, 전부 HTTP 200·동일 응답 |
| on | 213.99ms | 5.61~17.41ms | L1에 없는 학생으로 fallback 확인, 전부 HTTP 200·동일 응답 |

5.5초 뒤 재조회해 게시와 on의 local hit 복구를 확인했다.
장애 전에 로그인한 JWT로 GET을 호출한 테스트다. Redis 장애 중 로그인/refresh, Redis 영속 복구·HA까지 검증한 것은 아니다.

## 검증 상태와 운영 판단

- HTTP 통합 테스트 1건이 위 API 호출 42,442건을 실행해 통과했다. 의도한 무인증·강제 롤백 오류는 성능 GET 성공 집계와 구분한다.
- 수정 후 전체 회귀는 **831건 중 827건 통과, 실패/오류 0건, 선택 실행 테스트 4건 제외**다.
  제외는 기존 HTTP 벤치마크 2개, 서비스 캐시 벤치마크, 이번 HTTP 장시간 테스트다. 이번 HTTP 테스트는 별도 실행으로 통과했다.
- 운영 기본값은 off로 유지한다. 기능 정합성은 on에서도 통과했고 수강내역의 이득도 확인했지만,
  실제 hit율과 전체 이수학점 지연을 모르는 상태에서 모든 API의 성능 개선을 약속하지 않는다.
- 운영 전환은 [배포 준비](DEV-121_CACHE_DEPLOYMENT.md)의 스키마/권한/구 요청 종료 조건을 충족한 뒤,
  짧은 shadow 관측 후 on의 이수학점 지연·DB 부하·hit율을 확인하는 순서다.

12명의 반복 조회가 모두 128개 L1 키 안에 들어가는 warm 실험이다.
다수 사용자·잦은 변경·L1 eviction·다중 앱의 실제 운영 hit율과 운영 용량을 대표하지 않는다.
이번 결과를 학사정책 전체 정확성이나 운영 SLA로 일반화하지 않는다.

## 실행과 근거

```sh
DOCKER_HOST=unix:///Users/jinhyeon/.docker/run/docker.sock \
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock \
TESTCONTAINERS_HOST_OVERRIDE=127.0.0.1 \
RUN_TAKEN_LECTURE_CACHE_HTTP=true \
./gradlew bootJar test --tests '*TakenLectureCacheHttpIntegrationTest' \
  --offline --no-daemon --no-build-cache
```

로컬에 있는 고정 이미지 `mysql:8.4.8`, `redis:7.4.11-alpine`만 사용한다. 자동 pull하지 않는다.
실행 플래그가 없으면 컨테이너·앱을 시작하지 않는다. 기존 스냅샷·다른 작업의 컨테이너를 사용하지 않는다.

- [실제 HTTP 테스트 코드](../src/test/java/com/plzgraduate/myongjigraduatebe/takenlecture/infrastructure/adapter/persistence/TakenLectureCacheHttpIntegrationTest.java)
- [RR 경계 수정](../src/main/java/com/plzgraduate/myongjigraduatebe/graduation/application/service/CalculateSingleDetailGraduationService.java)
- [405개 측정값·환경·소스 해시](reviews/dev121-http-benchmark-20261029.json)
- [45개 조건별 비교·반복별 p95](reviews/dev121-http-comparison-20261029.json)
- [HTTP 및 전체 회귀 검증](reviews/dev121-http-verification-20261029.json)
- HTTP HTML: `build/reports/tests/dev121-http-20261029/index.html`
- 전체 회귀 HTML: `build/reports/tests/dev121-http-full-20261029/index.html`
