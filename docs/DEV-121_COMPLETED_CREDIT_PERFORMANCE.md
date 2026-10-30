# DEV-121 전체 이수학점 API 중복 조회 제거

대상: `GET /api/v1/graduations/credits` / 실행일: 2026-10-29 / 브랜치: `fix/DEV-121`

on의 사용자 재조회와 조회용 저장 요약 읽기를 제거해 실제 SELECT를 **10회 → 8회**로 줄였다.
확대 예열의 9개 조건에서는 p95가 같은 코드의 off보다 5.3~21.9%, 수정 전 on보다 5.4~27.6% 낮았다.
짧은 예열에서는 일부 조건이 악화한 결과도 그대로 보존한다. 서로 다른 프로토콜의 숫자를 섞지 않는다.

## 확인한 원인

이전 HTTP 실험에서 on의 이수학점 p95가 일부 조건에서 악화됐다. 코드와 MySQL의 실제 SELECT를 확인하면,
캐시가 수강 목록 JOIN을 생략해도 사용자/버전 조회가 추가돼 **off와 on 모두 10회 SELECT**를 보냈다.
이수학점 API 앞부분에서 사용자를 읽고, 내부 캐시가 같은 사용자를 버전과 함께 다시 읽고 있었다.
또한 계산 응답에 필요하지 않은 `completed_credit` 저장 행도 읽었다.

정확히 확인한 것은 캐시 사용 시 SQL 절감이 상쇄되는 구조적 비용이다. 이전 특정 p95 악화분 전체를
이 원인 하나로 설명하거나, 측정하지 않은 CPU·GC를 원인으로 단정하지 않는다.

## 사용한 방법과 선택 이유

| 방법 | 적용한 이유 | 이번에 확인하거나 바꾼 것 |
| --- | --- | --- |
| MySQL `Com_select` + Performance Schema digest | 코드상 repository 호출 수와 실제 서버 SELECT 수는 다를 수 있음 | 요청 전체 SELECT 수와 정규화된 SQL 종류를 함께 기록 |
| `TakenLectureRead` Java record | 이미 읽은 사용자와 수강내역을 계산으로 전달할 필요가 있음 | 요청 전용 사용자·inventory를 묶어 반환하고 사용자 재조회 제거 |
| 조회와 저장의 입력 분리 | 응답에는 계산된 학점만 필요하고 저장 행 ID는 필요하지 않음 | 조회는 빈 기존 요약 목록으로 새 모델 생성, 쓰기는 기존 ID 조회·갱신 유지 |
| Micrometer/Prometheus JVM 지표 | 지연시간 편차와 실행 중 컴파일·GC를 함께 살펴볼 필요가 있음 | 앱의 기존 compilation/GC counter를 요청 배치 전후에 수집 |
| 동일 JAR 비교 실험 | 이전 측정일의 숫자만 비교하면 실행 환경 변화가 섞일 수 있음 | 직전 JAR와 수정 JAR을 같은 합성 DB·Redis에서 번갈아 실행 |

Redis나 JVM 튜닝으로 우회한 것이 아니다. 정합성 확인에 필요한 DB 조회 결과를 재사용했다.
새 프레임워크나 배포 환경변수, 추가 마이그레이션은 없다.
캐시 JSON과 v3 키 형식도 그대로여서 이 변경만으로 namespace를 교체할 필요는 없다.

## 조회 경로의 변화

이전 on: 사용자 조회 → 저장 요약 조회 → 졸업 계산 → 사용자+revision+catalog revision 재조회 → L1/Redis 수강내역 → 정책 조회·계산.

개선 on: 사용자+revision+catalog revision 조회 → L1/Redis 수강내역 → 같은 사용자·inventory로 정책 조회·계산 → 응답 모델 생성.

`FindTakenLectureService.findTakenLecturesWithUser`는 off/안전하지 않은 트랜잭션에서는 기존 DB 경로로
사용자와 수강 목록을 읽는다. 캐시 사용이 가능한 경우에는 `CachedTakenLectureAdapter`가 이미 읽은
DB 사용자와 수강 스냅샷으로 `TakenLectureRead`를 만든다. `FindCompletedCreditService`가 이를 받고,
졸업 계산의 새 overload에 inventory를 직접 전달해 내부에서 다시 읽지 않는다.

`GenerateOrModifyCompletedCreditService.generateOrModifyCompletedCredit` 쓰기 경로는 기존 저장 요약을 계속 읽는다.
조회 경로만 기존 요약 목록을 비워서 매핑한다. HTTP 응답 DTO에는 저장 ID가 없고, 학점 값은 원래도 재계산하고 있었다.
저장 학점이 잘못돼 있어도 읽기 요청이 DB 요약을 자동 수정하는 동작은 추가하지 않았다.

코드 읽는 순서:

1. [TakenLectureRead](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/domain/model/TakenLectureRead.java): 사용자와 inventory를 함께 전달하는 자료형.
2. [FindTakenLectureService](../src/main/java/com/plzgraduate/myongjigraduatebe/takenlecture/application/service/find/FindTakenLectureService.java): 캐시/DB 경로에서 자료형을 만드는 곳.
3. [FindCompletedCreditService](../src/main/java/com/plzgraduate/myongjigraduatebe/completedcredit/application/service/FindCompletedCreditService.java): 같은 입력을 이수학점 계산에 전달하는 바깥 RR 경계.
4. [GenerateOrModifyCompletedCreditService](../src/main/java/com/plzgraduate/myongjigraduatebe/completedcredit/application/service/GenerateOrModifyCompletedCreditService.java): 읽기의 기존 요약 조회를 제거하고 쓰기는 유지한 곳.
5. [CalculateGraduationService](../src/main/java/com/plzgraduate/myongjigraduatebe/graduation/application/service/CalculateGraduationService.java): 전달받은 수강 inventory로 계산하는 overload.

## 실제 SQL 결과

아래 수치는 이번 합성 NORMAL 학생 조건의 요청 전체 SELECT다. 학적·전공 구성에 따라 정책 조회 수는 달라진다.

| 모드 | 수정 전 | 개선 후 | 차이 |
| --- | ---: | ---: | --- |
| off | 10 | 9 | 저장 요약 SELECT 제거 |
| shadow | 11 | 9 | 저장 요약 + 별도 사용자 SELECT 제거 |
| on | 10 | 8 | 저장 요약 + 별도 사용자 SELECT 제거 |

on의 8회는 사용자/두 버전 1회, 정책 관련 6회, JDBC 트랜잭션 격리 수준 확인 1회다.
off는 수강 목록 JOIN 1회가 추가된다. 공유 정책 캐시는 두 JAR/모드 모두 off로 고정했다.
이 숫자는 SQL SELECT 수이며 네트워크 명령 전체나 COMMIT·SET 횟수를 뜻하지 않는다.

## 유지한 정합성

- 캐시를 쓰기 전에 매 요청 DB에서 사용자와 현재 두 revision을 읽는다. DB 확인을 생략하거나 revision을 L1에 저장하지 않는다.
- API 바깥의 read-only REPEATABLE_READ 안에서 사용자·버전·수강내역·정책을 읽는다. 요청 시작 후 커밋된 변경은 다음 스냅샷부터 보이는 것이 정상이다.
- 학생 revision 증가, 쓰기 전 사용자 잠금, 과목 카탈로그 트리거, 버전 키, 성공한 read commit 후 게시를 유지한다.
- 공유 캐시에는 User나 수정 가능한 inventory를 저장하지 않는다. 요청마다 현재 DB 사용자와 별도 도메인 객체를 조합한다.
- Redis 실패 시 DB fallback, timeout·동시 실행 제한·cooldown, DB 조회 실패 시 오래된 값 반환 금지를 유지한다.
- 쓰기 경로의 저장 요약 ID·학점 갱신, 트랜잭션 롤백을 유지한다.

새 회귀 검사는 같은 RR 안에서 사용자 정보가 일관되고, 다음 요청의 L1 hit에서도 바뀐 사용자 정보를
DB에서 새로 읽는지를 확인한다. HTTP 검사에서는 동시 추가/삭제 중 목록과 이수학점 응답을 각각 검사한다.
서로 다른 두 HTTP 요청은 별도 스냅샷이므로 동시에 같은 결과여야 한다고 가정하지 않는다.

## 실험 방법과 한계

직전 검증 JAR의 SHA-256은 `862d5098b036e2e4337af9dc7b362bc6174ac003831ac632aceffc714cc8d458`다.
이 값은 [이전 HTTP 검증 증거](reviews/dev121-http-verification-20261029.json)의 JAR 해시와 같다.
비교용 바이너리는 ignored `build/dev121/baseline-credits/previous.jar`에 보관한다.

합성 학생·과목·실제 인증·컨트롤러·서비스·JPA를 사용한다. 정책·인증·캐시 대역을 넣지 않는다.
두 JAR 각각 off/shadow/on을 3회 실행하며, JAR 순서와 모드 순서를 바꾼다.
30/60/120과목 × 동시성 1/4/8 × GET 5종 × 96요청 × 3회 × 3모드 × 2JAR = 77,760건을 비교한다.
표의 p95는 조건별 3회 p95의 중앙값이며, 서로 다른 조건을 합친 p95가 아니다.

운영 DB나 원본 스냅샷은 사용하지 않는다. MySQL 8.4.8·Redis 7.4.11 계열과 prod 프로파일을 사용한 로컬 검증이며,
실제 운영의 패치 버전·TLS·네트워크·자원 제한을 그대로 복제한 것은 아니다.
합성 NORMAL/20학번/영어영문학전공 조건은 모든 학적·실제 졸업정책을 인증하지 않는다.
외부 앱 JVM CPU 시간은 호스트에서 얻을 수 없으면 unavailable로 기록하며, 0ms로 대체하지 않는다.

## 짧은 예열로 실행한 첫 수정 전후 비교

[첫 비교 원시 결과](reviews/dev121-credits-paired-20261029.json)와
[계산한 비교표](reviews/dev121-credits-paired-comparison-20261029.json)를 보존했다.
77,760건의 성능 GET은 모두 HTTP 200·동일 응답이었다. 총 API 호출은 84,604건이며,
의도한 인증 거절·롤백 오류 응답을 포함한다. 9개 정합성·장애 검사 묶음도 통과했다.
그러나 p95는 개선 코드의 on/off 비교에서 7/9개, 수정 전 on과의 비교에서는 4/9개 조건만 개선됐다.
쿼리 수 감소만으로 지연시간이 일관되게 개선됐다고 선언하지 않는다.

단위 ms, 각 조건의 3회 p95 중앙값.

| 과목 수 | 동시성 | 수정 전 on | 개선 off | 개선 shadow | 개선 on | 개선 on / 개선 off | 개선 on / 수정 전 on |
| --- | ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 30 | 1 | 12.88 | 23.06 | 18.98 | 12.27 | -46.8% | -4.7% |
| 30 | 4 | 15.71 | 24.17 | 23.56 | 18.80 | -22.2% | +19.7% |
| 30 | 8 | 19.93 | 22.66 | 33.83 | 18.74 | -17.3% | -5.9% |
| 60 | 1 | 9.22 | 14.02 | 13.65 | 10.80 | -22.9% | +17.2% |
| 60 | 4 | 14.21 | 19.05 | 22.52 | 13.15 | -31.0% | -7.5% |
| 60 | 8 | 23.18 | 25.13 | 25.40 | 25.76 | +2.5% | +11.2% |
| 120 | 1 | 8.69 | 9.15 | 12.53 | 8.91 | -2.7% | +2.5% |
| 120 | 4 | 15.07 | 15.58 | 16.27 | 12.44 | -20.1% | -17.4% |
| 120 | 8 | 17.94 | 18.57 | 23.45 | 21.04 | +13.3% | +17.3% |

이 프로토콜은 JVM마다 총 300개 GET을 예열하지만, 전체 이수학점 API는 그중 60회뿐이다.
SQL 수·계산 규칙을 바꾸지 않은 다른 API도 두 JAR 사이에 p95 중앙값 기준 약 6~16% 느려지는 편차가 있었다.
따라서 추가로 예열량을 늘리고 JIT 컴파일 시간·GC pause를 계측하는 조건을 분리했다.
첫 실험의 편차를 JIT·GC 탓으로 확정한 것은 아니다. 다른 조건의 결과로 첫 실험을 대체하지 않는다.

## 예열량을 늘린 후속 비교 방법

같은 두 JAR·합성 DB·Redis·JVM 설정을 유지하고 전체 이수학점 API만 비교한다.
각 JAR의 off/on을 순서를 바꿔 3회씩 실행한다. 매 앱에서 4개 클라이언트로 최소 3,072건·10초 이상
이수학점 GET을 예열하고 1.5초 쉬었다. 각 측정 조건은 96건에서 384건으로 늘렸다.
9개 과목 수/동시성 조건 × 384건 × 2JAR × 2모드 × 3회 = **41,472건**이다.

`DEV121_HTTP_STEADY`는 이 테스트 절차를 고르는 이름이다. JVM의 완전한 안정 상태를 보장하는 옵션은 아니다.
실제로 확대 예열 뒤에도 측정 중 컴파일과 GC가 발생했다. 배치마다 기존 Prometheus의
`jvm_compilation_time_ms_total`, `jvm_gc_pause_seconds_sum/count` 증분을 기록한다.
컴파일 시간과 GC pause 합계는 요청 시간에서 빼지 않는다. 동시 실행과 개별 요청과의 겹침을 완전히 알 수 없기 때문이다.
별도 프로세스 CPU 시간은 계속 unavailable로 두었다.

기능·장애 검사는 첫 두 JAR 비교에서 수행했고, 이 후속 실행은 같은 코드의 off/on 응답 일치·성능을 집중 검증한다.

## 확대 예열 결과

[확대 예열 원시 결과](reviews/dev121-credits-expanded-warmup-20261029.json)와
[재계산 가능한 비교표](reviews/dev121-credits-expanded-comparison-20261029.json)를 보존했다.
성능 GET 41,472건은 모두 HTTP 200·동일 응답이고, 예열 GET은 총 42,048건이었다.
실제 API 호출은 83,822건이다. 모든 on 배치에서 384건 전부 L1 hit였고 error/mismatch와 GET 쓰기는 0이었다.

9개 조건 모두 개선 코드의 on이 같은 코드의 off보다 빨랐다(p95 **5.3~21.9% 감소**).
수정 전 on과 비교해도 9개 조건 모두 빨랐다(p95 **5.4~27.6% 감소**).
아래는 같은 확대 예열 프로토콜 내 비교다. 첫 짧은 예열 결과와 숫자를 섞지 않는다.

단위 ms, 3회 p95 중앙값.

| 과목 수 | 동시성 | 수정 전 on | 개선 off | 개선 on | 개선 on / 개선 off | 개선 on / 수정 전 on |
| --- | ---: | ---: | ---: | ---: | ---: | ---: |
| 30 | 1 | 8.86 | 8.27 | 6.95 | -15.9% | -21.5% |
| 30 | 4 | 12.46 | 12.59 | 11.79 | -6.3% | -5.4% |
| 30 | 8 | 16.91 | 16.41 | 15.55 | -5.3% | -8.1% |
| 60 | 1 | 8.10 | 7.43 | 6.96 | -6.3% | -14.1% |
| 60 | 4 | 15.63 | 12.65 | 11.38 | -10.1% | -27.2% |
| 60 | 8 | 18.78 | 17.40 | 13.59 | -21.9% | -27.6% |
| 120 | 1 | 8.86 | 8.39 | 7.05 | -15.9% | -20.4% |
| 120 | 4 | 14.13 | 13.76 | 10.96 | -20.4% | -22.4% |
| 120 | 8 | 17.22 | 18.80 | 15.81 | -15.9% | -8.2% |

108개 배치 중 108개에서 컴파일 시간 증가가 있었고,
GC pause 합계는 배치당 중앙값 7.0ms, 최대 44.0ms였다.
이는 단일 요청의 pause나 최장 단일 GC pause가 아니라 배치 구간의 합계다.
실행 중 컴파일·GC가 있다는 사실은 확인했지만, 앞선 특정 p95 악화분의 원인을 하나로 확정하지 않는다.

두 실험을 합쳐 성능 GET **119,232건**의 모든 응답을 비교했다. 긴 예열에서의 성능 이득과
짧은 예열의 비일관적인 결과를 함께 제시한다. 운영 기본 off는 유지하고 실제 운영 SLO·트래픽 검증 없이
on 전환을 완료했다고 판단하지 않는다.

## 최종 검증

- 실제 JAR HTTP 비교 두 차례 모두 테스트 통과. 성능 GET 119,232건의 모든 응답 필드가 기준 DB 응답과 일치했다.
- 동시 목록·이수학점 조회와 추가/삭제, 커밋 후 조회, 저장 단계 오류 롤백, 카탈로그 변경, Redis 중단·복구의 9개 검사 묶음 통과.
- 전체 JUnit **834건: 830건 통과, 실패/오류 0, 선택 실행 제외 4**. HTTP 비교는 별도 선택 실행 두 차례 통과이며 전체 회귀의 제외 4건에 HTTP 테스트가 포함된다.
- 같은 RR의 사용자 일관성 / 다음 요청의 L1 hit에서도 사용자 정보 갱신, 중복 사용자 조회 방지, 조회에서 요약 DB 접근·쓰기 없음 회귀 검사 통과.
- 모든 성능 GET의 INSERT/UPDATE/DELETE, 캐시 error/mismatch는 0. Redis 장애 주입의 의도한 error/fallback은 별도 기능 검사다.
- 생성한 설정 파일 0개, 이 테스트 소유 컨테이너 0개를 확인했다. 다른 작업의 컨테이너는 건드리지 않았다.
- 소스 63개·두 JAR·원시/비교 JSON의 SHA-256과 실행 테스트 결과는 [검증 증거](reviews/dev121-credits-verification-20261029.json)에 기록했다.

운영 배포·GitHub 환경변수 변경·커밋·푸시는 하지 않았다. 운영 기본 off는 유지한다.
후속 배포 준비는 DEV-121 선배포(V11 → V14/V15)로 확정했으며 DEV-116을 기다리지 않는다.
최신 순서·검증·권한 확인 절차는 [배포 준비](DEV-121_CACHE_DEPLOYMENT.md)를 따른다.

## 재실행

비교 전 JAR를 보관한 상태에서 다음을 실행한다. 이전 바이너리가 없으면 `DEV121_HTTP_BASELINE_JAR`를 생략해
개선 코드의 세 모드만 검증할 수 있다. 그 결과는 두 JAR 성능 비교로 해석하지 않는다.

```sh
DOCKER_HOST=unix:///Users/jinhyeon/.docker/run/docker.sock \
TESTCONTAINERS_DOCKER_SOCKET_OVERRIDE=/var/run/docker.sock \
TESTCONTAINERS_HOST_OVERRIDE=127.0.0.1 \
RUN_TAKEN_LECTURE_CACHE_HTTP=true \
DEV121_HTTP_BASELINE_JAR=build/dev121/baseline-credits/previous.jar \
./gradlew bootJar test --tests '*TakenLectureCacheHttpIntegrationTest' \
  --offline --no-daemon --no-build-cache
```

완료된 원시 report를 `infra/cache/compare_http_results.py REPORT OUTPUT`에 넘기면 집계가 재생성된다.
미완료 run, 한쪽 JAR만 있는 run, 반복이 누락된 조건은 이 비교 스크립트가 거절한다.

확대 예열 비교는 위 명령에 `DEV121_HTTP_STEADY=true`를 추가한다. 이때 shadow 성능은 재측정하지 않는다.
원시 결과의 `modeOrder`가 실제 실행 순서이며, 단축/확대 프로토콜을 구분해서 읽는다.
