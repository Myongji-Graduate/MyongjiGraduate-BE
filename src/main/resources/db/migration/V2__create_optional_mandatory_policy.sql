CREATE TABLE optional_mandatory_policy (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    major VARCHAR(100) NOT NULL,
    required_count INT NOT NULL,
    required_credit INT NOT NULL,
    start_entry_year INT NOT NULL,
    end_entry_year INT NOT NULL,
    major_type VARCHAR(20) NULL,
    policy_version INT NOT NULL DEFAULT 1,
    status VARCHAR(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_optional_mandatory_policy_version
        UNIQUE (major, name, start_entry_year, end_entry_year, policy_version),
    CONSTRAINT ck_optional_mandatory_policy_count CHECK (required_count > 0),
    CONSTRAINT ck_optional_mandatory_policy_credit CHECK (required_credit >= 0),
    CONSTRAINT ck_optional_mandatory_policy_year CHECK (start_entry_year <= end_entry_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

CREATE INDEX idx_optional_mandatory_policy_lookup
    ON optional_mandatory_policy (major, start_entry_year, end_entry_year, status);

CREATE TABLE optional_mandatory_policy_lecture (
    id BIGINT NOT NULL AUTO_INCREMENT,
    policy_id BIGINT NOT NULL,
    lecture_id VARCHAR(255) NOT NULL,
    equivalence_key VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT uk_optional_mandatory_policy_lecture UNIQUE (policy_id, lecture_id),
    CONSTRAINT fk_optional_mandatory_policy_lecture_policy
        FOREIGN KEY (policy_id) REFERENCES optional_mandatory_policy (id),
    CONSTRAINT fk_optional_mandatory_policy_lecture_lecture
        FOREIGN KEY (lecture_id) REFERENCES lecture (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

INSERT INTO optional_mandatory_policy
    (name, major, required_count, required_credit, start_entry_year, end_entry_year,
     major_type, policy_version, status)
VALUES
    ('행정학전공 선택필수', '행정학전공', 2, 6, 17, 24, NULL, 1, 'ACTIVE'),
    ('경영학전공 선택필수', '경영학전공', 1, 3, 8, 24, NULL, 1, 'ACTIVE'),
    ('경영정보학과 선택필수', '경영정보학과', 2, 6, 19, 24, NULL, 1, 'ACTIVE'),
    ('경영정보학과 선택필수', '경영정보학과', 2, 6, 25, 99, NULL, 1, 'ACTIVE'),
    ('국제통상학전공 선택필수', '국제통상학전공', 4, 12, 9, 24, NULL, 1, 'ACTIVE');

INSERT INTO optional_mandatory_policy_lecture (policy_id, lecture_id, equivalence_key)
SELECT p.id, candidate.lecture_id, candidate.equivalence_key
FROM (
    SELECT '행정학전공' major, 'HBA01109' lecture_id, 'HBA01109' equivalence_key UNION ALL
    SELECT '행정학전공', 'HBA01110', 'HBA01110' UNION ALL
    SELECT '행정학전공', 'HBA01111', 'HBA01111' UNION ALL
    SELECT '행정학전공', 'HBA01112', 'HBA01112' UNION ALL
    SELECT '행정학전공', 'HBA01113', 'HBA01113' UNION ALL
    SELECT '행정학전공', 'HBA01222', 'HBA01222' UNION ALL
    SELECT '경영학전공', 'HBX01128', 'HBX01128' UNION ALL
    SELECT '경영학전공', 'HBX01127', 'HBX01127' UNION ALL
    SELECT '경영학전공', 'HBX01125', 'BUSINESS_INFORMATION' UNION ALL
    SELECT '경영학전공', 'HBY01103', 'BUSINESS_INFORMATION' UNION ALL
    SELECT '경영정보학과', 'HBX01104', 'HBX01104' UNION ALL
    SELECT '경영정보학과', 'HBX01113', 'HBX01113' UNION ALL
    SELECT '경영정보학과', 'HBX01106', 'HBX01106' UNION ALL
    SELECT '경영정보학과', 'HBX01105', 'FINANCIAL_MANAGEMENT' UNION ALL
    SELECT '경영정보학과', 'HBX01147', 'FINANCIAL_MANAGEMENT' UNION ALL
    SELECT '경영정보학과', 'HBX01114', 'OPERATIONS_MANAGEMENT' UNION ALL
    SELECT '경영정보학과', 'HBX01143', 'OPERATIONS_MANAGEMENT' UNION ALL
    SELECT '국제통상학전공', 'HBX01104', 'HBX01104' UNION ALL
    SELECT '국제통상학전공', 'HBX01113', 'HBX01113' UNION ALL
    SELECT '국제통상학전공', 'HBX01106', 'HBX01106' UNION ALL
    SELECT '국제통상학전공', 'HBX01105', 'FINANCIAL_MANAGEMENT' UNION ALL
    SELECT '국제통상학전공', 'HBX01147', 'FINANCIAL_MANAGEMENT' UNION ALL
    SELECT '국제통상학전공', 'HBX01114', 'OPERATIONS_MANAGEMENT' UNION ALL
    SELECT '국제통상학전공', 'HBX01143', 'OPERATIONS_MANAGEMENT'
) candidate
JOIN optional_mandatory_policy p ON p.major = candidate.major AND p.policy_version = 1
WHERE NOT (p.major = '경영정보학과' AND p.start_entry_year >= 25)
   OR candidate.lecture_id IN ('HBX01113', 'HBX01106', 'HBX01147');
