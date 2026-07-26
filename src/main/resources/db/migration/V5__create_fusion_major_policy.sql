-- 융합전공 과목은 원 소속 학과의 전공/일반교양으로 계산하지 않는다.
-- 현재 공식 교육과정에서 확인된 인문캠퍼스 두 융합전공부터 적용한다.

CREATE TABLE fusion_major_policy (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fusion_major VARCHAR(100) NOT NULL,
    required_credit INT NOT NULL,
    start_entry_year INT NOT NULL,
    end_entry_year INT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_fusion_major_policy_scope
        UNIQUE (fusion_major, start_entry_year, end_entry_year),
    CONSTRAINT ck_fusion_major_policy_credit CHECK (required_credit > 0),
    CONSTRAINT ck_fusion_major_policy_year CHECK (start_entry_year <= end_entry_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE TABLE fusion_major_lecture (
    id BIGINT NOT NULL AUTO_INCREMENT,
    policy_id BIGINT NOT NULL,
    lecture_id VARCHAR(255) NOT NULL,
    equivalence_key VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_fusion_major_lecture UNIQUE (policy_id, lecture_id),
    CONSTRAINT fk_fusion_major_lecture_policy
        FOREIGN KEY (policy_id) REFERENCES fusion_major_policy (id),
    CONSTRAINT fk_fusion_major_lecture_lecture
        FOREIGN KEY (lecture_id) REFERENCES lecture (id),
    INDEX idx_fusion_major_lecture_id (lecture_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO fusion_major_policy
    (fusion_major, required_credit, start_entry_year, end_entry_year)
VALUES
    ('응용데이터사이언스', 36, 16, 99),
    ('인문ICT콘텐츠', 36, 16, 99);

INSERT INTO fusion_major_lecture (policy_id, lecture_id, equivalence_key)
SELECT policy.id, course.lecture_id, course.equivalence_key
FROM fusion_major_policy policy
JOIN (
    SELECT 'HED00201' lecture_id, 'FUSION_R_STATISTICS' equivalence_key UNION ALL
    SELECT 'HED00202', 'FUSION_BASIC_PROGRAMMING' UNION ALL
    SELECT 'HED00203', 'FUSION_DATA_VISUALIZATION' UNION ALL
    SELECT 'HED00204', 'FUSION_ADVANCED_R_OR_ML' UNION ALL
    SELECT 'HED00205', 'FUSION_DATABASE' UNION ALL
    SELECT 'HED00206', 'FUSION_CAPSTONE' UNION ALL
    SELECT 'HED00207', 'FUSION_R_STATISTICS' UNION ALL
    SELECT 'HED00208', 'FUSION_ADVANCED_R_OR_ML' UNION ALL
    SELECT 'HED01208', 'FUSION_DATABASE'
) course
JOIN lecture ON lecture.id = course.lecture_id;

DELETE FROM major
WHERE lecture_id IN (
    'HED00201', 'HED00202', 'HED00203', 'HED00204',
    'HED00205', 'HED00206', 'HED00207', 'HED00208',
    'HED01208'
);
