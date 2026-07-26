# parsing_text_history

- Entity: `ParsingTextHistoryJpaEntity`
- 설명: 성적표 파싱 텍스트와 파싱 결과 이력
- 공통 컬럼: `TimeBaseEntity` 상속으로 `created_at`, `updated_at` 포함

| 컬럼 | Java 타입 | DB 타입 추정 | 제약/관계 | 비고 |
| --- | --- | --- | --- | --- |
| `id` | `Long` | `BIGINT` | PK, auto increment | 내부 식별자 |
| `user_id` | `UserJpaEntity` | `BIGINT` | nullable, FK -> `user.id` | 회원 요청만 연결하며 비회원 요청은 `NULL` |
| `parsing_text` | `String` | `VARCHAR(5000)` |  | `@Column(length = 5000)` |
| `parsing_result` | `ParsingResult` | `VARCHAR` |  | `EnumType.STRING` |
| `failure_reason` | `FailureReason` | `VARCHAR(100)` |  | `EnumType.STRING`, Flyway V1에서 추가 |
| `failure_details` | `String` | `TEXT` |  | 실패 상세 정보, Flyway V1에서 추가 |
| `tracking_code` | `String` | `VARCHAR(36)` | nullable, unique | 비회원 검사 건별 문의용 UUID, Flyway V8에서 추가 |
| `requester_type` | `ParsingRequesterType` | `VARCHAR(20)` | not null | `MEMBER` 또는 `ANONYMOUS`, Flyway V8에서 추가 |
| `created_at` | `Instant` | `TIMESTAMP` | not null, updatable false | 생성 시각 |
| `updated_at` | `Instant` | `TIMESTAMP` | not null | 수정 시각 |

비회원 졸업검사는 성공과 실패를 모두 저장한다. 성공 이력은 이용량과 성공률 집계에,
실패 이력은 오류 분석과 사용자 문의 대응에 사용한다. 회원 이력과 달리 `user_id`는
`NULL`이며, API 응답의 `tracking_code`로 개별 요청을 찾는다.
