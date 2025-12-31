# Flyway 사용 가이드

## 개요

Flyway는 데이터베이스 스키마 마이그레이션을 버전 관리하고 자동화하는 도구

## 기본 개념

### 1. 마이그레이션 파일 위치
```
src/main/resources/db/migration/
```

### 2. 파일 네이밍 규칙
```
V{version}__{description}.sql
```
- `V`: 버전 접두사 (필수)
- `{version}`: 버전 번호 (1, 2, 3, ... 또는 1.1, 1.2, ...)
- `__`: 구분자 (언더스코어 2개, 필수)
- `{description}`: 설명 (선택사항, 언더스코어/하이픈 사용 가능)

**예시:**
```
V1__add_failure_reason_and_details.sql
V2__add_user_email_column.sql
V3__create_index_on_user_id.sql
```

### 3. 실행 순서
- 버전 번호 순서대로 실행됩니다
- 한 번 실행된 마이그레이션은 다시 실행되지 않습니다
- 실행 내역은 `flyway_schema_history` 테이블에 기록됩니다

## 작업 흐름

### 새로운 마이그레이션 추가하기

1. **마이그레이션 파일 생성**
   ```bash
   # src/main/resources/db/migration/ 폴더에 새 파일 생성
   V{다음버전}__{설명}.sql
   ```

2. **SQL 작성**
   ```sql
   -- V2__add_user_email.sql
   ALTER TABLE user ADD COLUMN email VARCHAR(255) NULL;
   ```

3. **커밋 & 배포**
   - Git에 커밋하면 자동으로 감지됩니다
   - 프로덕션 환경에서 애플리케이션 시작 시 자동 실행됩니다

### 마이그레이션 실행 확인

애플리케이션 로그에서 확인:
```
Flyway Migrations Successfully Applied
```

또는 데이터베이스에서 직접 확인:
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

## 환경별 설정

### 로컬/개발 환경
```yaml
spring:
  flyway:
    enabled: false  # ddl-auto: update 사용
```

### 프로덕션 환경
```yaml
spring:
  flyway:
    enabled: true
    baseline-on-migrate: true  # 기존 DB를 baseline으로 인식
    baseline-version: 0
    locations: classpath:db/migration
```

## 주의사항

### ❌ 하지 말아야 할 것

1. **이미 실행된 마이그레이션 파일 수정 금지**
   - 파일을 수정하면 체크섬 오류가 발생합니다
   - 새로운 마이그레이션을 만들어야 합니다

2. **버전 번호 건너뛰지 말기**
   - V1, V2, V3 순서대로 작성
   - 중간에 버전을 건너뛰면 문제가 될 수 있습니다

3. **롤백 마이그레이션 자동 실행 안 됨**
   - Flyway는 롤백을 자동으로 제공하지 않습니다
   - 필요시 수동으로 롤백 스크립트를 작성해야 합니다

### ✅ 권장 사항

1. **작은 단위로 분리**
   - 하나의 마이그레이션에 너무 많은 변경을 넣지 마세요
   - 테이블 하나, 컬럼 추가 하나 등 단위로 분리

2. **idempotent하게 작성 (가능한 경우)**
   ```sql
   -- 좋은 예: 여러 번 실행해도 안전
   CREATE TABLE IF NOT EXISTS my_table (...);
   
   -- 나쁜 예: 이미 존재하면 에러
   CREATE TABLE my_table (...);
   ```

3. **의미있는 설명 작성**
   - 파일명의 description 부분을 명확하게 작성
   - 나중에 히스토리를 볼 때 이해하기 쉽습니다

## 문제 해결

### 마이그레이션 실패 시

1. **로그 확인**
   ```
   Flyway migration failed: ...
   ```

2. **DB 상태 확인**
   ```sql
   SELECT * FROM flyway_schema_history WHERE success = 0;
   ```

3. **수동으로 문제 해결 후 재시작**
   - 실패한 SQL을 수동으로 실행하거나
   - 실패한 마이그레이션을 수정 후 `flyway_schema_history`에서 해당 레코드 삭제

### 기존 DB에 Flyway 적용 (baseline)

프로덕션 환경에서는 이미 `baseline-on-migrate: true`로 설정되어 있습니다.
- 기존 테이블들을 버전 0으로 인식
- 새로운 마이그레이션(V1부터)부터 적용

## 예시: 새로운 마이그레이션 추가

```sql
-- 파일: src/main/resources/db/migration/V2__add_user_email.sql
ALTER TABLE user 
ADD COLUMN email VARCHAR(255) NULL COMMENT '사용자 이메일' 
AFTER name;

CREATE INDEX idx_user_email ON user(email);
```

1. 파일 생성
2. Git 커밋
3. 배포 시 자동 실행

## 유용한 명령어

### Gradle로 마이그레이션 정보 확인 (선택사항)

```bash
./gradlew flywayInfo
```

### 마이그레이션 실행 (개발용, 선택사항)

```bash
./gradlew flywayMigrate
```

## 참고 자료

- [Flyway 공식 문서](https://flywaydb.org/documentation/)
- [Spring Boot Flyway 통합](https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto.data-initialization.migration-tool.flyway)

