-- 2025학년도 기초프로그래밍 교과코드 개편 경과조치.
-- 구 학번도 새 교과코드를 재수강·대체 수강할 수 있다.
-- 데이터사이언스전공 표 전체가 전 학번 전필 해제 대상이므로
-- 응용소프트웨어·데이터테크놀로지 계열 모두 전선으로 매핑한다.

INSERT INTO major (lecture_id, major, mandatory, start_entry_year, end_entry_year)
SELECT mapping.lecture_id,
       mapping.major,
       mapping.mandatory,
       mapping.start_entry_year,
       mapping.end_entry_year
FROM (
    SELECT 'HEF01101' AS lecture_id, '응용소프트웨어전공' AS major, 0 AS mandatory, 16 AS start_entry_year, 99 AS end_entry_year
    UNION ALL SELECT 'HEF01102', '응용소프트웨어전공', 0, 16, 99
    UNION ALL SELECT 'HEF01101', '데이터테크놀로지전공', 0, 16, 24
    UNION ALL SELECT 'HEF01102', '데이터테크놀로지전공', 0, 16, 24
    UNION ALL SELECT 'HEF01101', '데이터사이언스전공', 0, 16, 99
    UNION ALL SELECT 'HEF01102', '데이터사이언스전공', 0, 16, 99
) mapping
WHERE EXISTS (
    SELECT 1
    FROM lecture
    WHERE lecture.id = mapping.lecture_id
)
AND NOT EXISTS (
    SELECT 1
    FROM major existing
    WHERE existing.lecture_id = mapping.lecture_id
      AND existing.major = mapping.major
      AND existing.mandatory = mapping.mandatory
      AND existing.start_entry_year = mapping.start_entry_year
      AND existing.end_entry_year = mapping.end_entry_year
);
