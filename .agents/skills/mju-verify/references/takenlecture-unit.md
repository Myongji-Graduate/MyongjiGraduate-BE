# 이수과목 서비스·캐시 단위 검증

## 기능과 진입점

사용자가 과목을 추가·삭제하고 이수 목록을 조회하는 경로 중 **백엔드 단위 계약**을
검증한다. 실제 사용자 UI 경로의 검증은 후속 단계다.

```sh
node tools/agent-harness/dist/cli.js verify takenlecture-unit --offline
```

## 고정 클래스와 관찰 대상

테스트 루트는 `src/test/java/com/plzgraduate/myongjigraduatebe/takenlecture/`다.

| 클래스 | 소스 경로 | 검증하는 것 |
| --- | --- | --- |
| TakenLectureCacheTest | infrastructure/adapter/cache/TakenLectureCacheTest.java | cache hit/fallback, 사용자·revision 구분, publication과 local cache 계약 |
| DeleteTakenLectureServiceTest | application/service/delete/DeleteTakenLectureServiceTest.java | 단일·전체 삭제 포트 호출 |
| GenerateTakenLectureServiceTest | application/service/save/GenerateTakenLectureServiceTest.java | 맞춤 과목 저장 포트 호출 |
| FindTakenLectureServiceTest | application/service/find/FindTakenLectureServiceTest.java | snapshot, 이수 목록과 학점 계산 |

첫 기준 `861228e4`에는 총 21개의 `@Test`가 선언돼 있다. 이 숫자는 앞으로의 고정 합격
기준이 아니다. CLI는 이번 실행의 결과를 검사하며 테스트를 추가하거나 제거하면 변경
이유와 완료 조건을 함께 검토한다.

## 성공 증거와 함정

- Java 21과 Gradle wrapper/의존성 캐시가 필요하다. 이 suite는 Spring context·실제 DB·Docker를 사용하지 않는다.
- cache 테스트의 Redis는 mock이다. 실제 Redis의 TTL이나 MySQL commit 정합성이 증명된 것은 아니다.
- 저장·삭제 포트 호출은 실제 저장 후 재조회·학점 재계산 완료의 증거가 아니다.
- 테스트명에 `TakenLecture`가 포함된 전체 클래스를 실행하면 DB 통합 테스트가 섞인다.
- CLI는 새 실행 디렉터리의 JUnit 결과와 프로세스 종료를 함께 확인한다. 빈 결과나 전부 skip은 성공이 아니다.
- 새로고침 유지, API 인증, 과목 수정 후 최종 졸업 결과는 이 suite의 완료 범위 밖이다.

## 후속 검증

`TakenLectureCacheConsistencyIntegrationTest`와 `TakenLectureCacheHttpIntegrationTest`는
별도 opt-in 합성 DB/Redis 환경을 사용한다. 필요한 경우
[합성 DB·HTTP 지도](takenlecture-integration.md)에서 해당 suite를 선택한다.
단위 명령이 이 통합 테스트나 프론트 Cypress까지 실행한다고 설명하지 않는다.
