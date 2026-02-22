# Grafana 대시보드  가이드

## 개요

성적표 파싱 실패 분석을 위한 Grafana 대시보드 구성 가이드입니다. 이 대시보드는 실패한 성적표 파싱 데이터를 시각화하여 실패 패턴을 분석하는 데 도움을 줍니다.

## 대시보드 패널 설명

### 1. 실패 건수 추이 (Time Series)

**위치**: 상단 왼쪽  
**타입**: Time Series Chart  
**설명**: 일별 실패 건수를 시간에 따라 추적합니다.

**SQL 쿼리**:
```sql
SELECT
  DATE(created_at) as time,
  COUNT(*) as failures
FROM parsing_text_history
WHERE parsing_result = 'FAIL'
GROUP BY DATE(created_at)
ORDER BY time;
```

### 2. 실패 원인별 분포 (Pie Chart)

**위치**: 상단 오른쪽  
**타입**: Pie Chart  
**설명**: 실패 원인별 건수를 원형 차트로 표시합니다.

**SQL 쿼리**:
```sql
SELECT
  COALESCE(failure_reason, 'UNKNOWN') as reason,
  COUNT(*) as count
FROM parsing_text_history
WHERE parsing_result = 'FAIL'
GROUP BY failure_reason
ORDER BY count DESC;
```

### 3. 최근 실패 목록 (Table)

**위치**: 중앙  
**타입**: Table  
**설명**: 최근 실패한 성적표 파싱 내역을 테이블로 표시합니다.

**SQL 쿼리**:
```sql
SELECT
  id,
  user_id,
  COALESCE(failure_reason, 'UNKNOWN') as failure_reason,
  created_at,
  LEFT(parsing_text, 50) as preview
FROM parsing_text_history
WHERE parsing_result = 'FAIL'
ORDER BY created_at DESC
LIMIT 50;
```

### 4. 일별 실패율 (Stat)

**위치**: 하단 왼쪽  
**타입**: Stat Panel  
**설명**: 오늘의 실패율을 백분율로 표시합니다.

**SQL 쿼리**:
```sql
SELECT
  COUNT(CASE WHEN parsing_result = 'FAIL' THEN 1 END) * 100.0 / COUNT(*) as failure_rate
FROM parsing_text_history
WHERE DATE(created_at) = CURDATE();
```

**임계값**:
- 초록색: 0% 미만
- 노란색: 5% 이상
- 빨간색: 10% 이상

### 5. 전체 실패 건수 (Stat)

**위치**: 하단 중앙 왼쪽  
**타입**: Stat Panel  
**설명**: 전체 실패 건수를 표시합니다.

**SQL 쿼리**:
```sql
SELECT COUNT(*) as total_failures
FROM parsing_text_history
WHERE parsing_result = 'FAIL';
```

### 6. 오늘 실패 건수 (Stat)

**위치**: 하단 중앙 오른쪽  
**타입**: Stat Panel  
**설명**: 오늘 발생한 실패 건수를 표시합니다.

**SQL 쿼리**:
```sql
SELECT COUNT(*) as today_failures
FROM parsing_text_history
WHERE parsing_result = 'FAIL'
  AND DATE(created_at) = CURDATE();
```

### 7. 미분석 실패 건수 (Stat)

**위치**: 하단 오른쪽  
**타입**: Stat Panel  
**설명**: 아직 실패 원인이 분석되지 않은 실패 건수를 표시합니다.

**SQL 쿼리**:
```sql
SELECT COUNT(*) as unanalyzed_failures
FROM parsing_text_history
WHERE parsing_result = 'FAIL'
  AND failure_reason IS NULL;
```

**임계값**:
- 초록색: 0건
- 노란색: 1건 이상
- 빨간색: 10건 이상

## 실패 원인 (FailureReason) 종류

대시보드에서 표시되는 실패 원인은 다음과 같습니다:

- `EMPTY_PARSING_TEXT`: PDF를 인식하지 못함
- `PARSING_EXCEPTION`: PDF 파싱 중 오류 발생
- `INCORRECT_STUDENT_NUMBER`: 학번이 일치하지 않음
- `UNSUPPORTED_STUDENT_CATEGORY`: 지원하지 않는 학생 유형
- `GRADUATION_CHECK_FAILED`: 졸업 검사 실패
- `LECTURE_NOT_FOUND`: 과목 정보를 찾을 수 없음
- `UNKNOWN_ERROR`: 알 수 없는 오류
- `UNKNOWN`: 실패 원인이 아직 분석되지 않음 (failure_reason이 NULL인 경우)

## 대시보드 커스터마이징

### 시간 범위 변경

대시보드 상단의 시간 선택기를 사용하여 원하는 시간 범위를 선택할 수 있습니다. 기본값은 최근 7일입니다.

### 새로고침 주기

대시보드 우측 상단의 새로고침 버튼을 클릭하거나, 자동 새로고침 주기를 설정할 수 있습니다. 기본값은 30초입니다.

### 패널 수정

각 패널의 제목을 클릭하여 패널 편집 메뉴에 접근할 수 있습니다. 여기서 SQL 쿼리, 시각화 옵션, 임계값 등을 수정할 수 있습니다.

## 문제 해결

### 데이터가 표시되지 않는 경우

1. **데이터 소스 연결 확인**
   - Grafana 설정 → Data Sources에서 MySQL 데이터 소스가 올바르게 연결되어 있는지 확인합니다.
   - Test 버튼을 클릭하여 연결을 테스트합니다.

2. **쿼리 실행 확인**
   - 각 패널의 쿼리를 직접 실행하여 데이터가 반환되는지 확인합니다.
   - MySQL 클라이언트에서 직접 쿼리를 실행해봅니다.

3. **권한 확인**
   - 데이터베이스 사용자에게 `parsing_text_history` 테이블에 대한 SELECT 권한이 있는지 확인합니다.

### 시간대 문제

- Grafana의 시간대 설정이 데이터베이스의 시간대와 일치하는지 확인합니다.
- `DATE(created_at)` 함수는 데이터베이스 서버의 시간대를 사용합니다.

## 추가 패널 아이디어

필요에 따라 다음 패널을 추가할 수 있음:

- **시간대별 실패 분포**: 시간대(시간)별 실패 건수 분석
- **사용자별 실패 통계**: 사용자별 실패 빈도 분석
- **실패 상세 정보**: failure_details 필드를 표시하는 상세 테이블
- **성공률 추이**: 성공과 실패를 함께 표시하는 비교 차트

## 참고 자료

- [Grafana 공식 문서](https://grafana.com/docs/)
- [Grafana MySQL 데이터 소스 설정](https://grafana.com/docs/grafana/latest/datasources/mysql/)
- [Grafana 대시보드 JSON 모델](https://grafana.com/docs/grafana/latest/dashboards/json-model/)

