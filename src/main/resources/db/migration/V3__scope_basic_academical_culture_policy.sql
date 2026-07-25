ALTER TABLE basic_academical_culture
    ADD COLUMN source_policy_key VARCHAR(255) NULL AFTER college,
    ADD COLUMN mapping_key VARCHAR(512) NULL AFTER source_policy_key,
    ADD COLUMN major VARCHAR(255) NULL AFTER mapping_key,
    ADD COLUMN start_entry_year INT NULL AFTER major,
    ADD COLUMN end_entry_year INT NULL AFTER start_entry_year,
    ADD COLUMN start_taken_year INT NULL AFTER end_entry_year,
    ADD COLUMN start_taken_semester VARCHAR(20) NULL AFTER start_taken_year,
    ADD COLUMN end_taken_year INT NULL AFTER start_taken_semester,
    ADD COLUMN end_taken_semester VARCHAR(20) NULL AFTER end_taken_year,
    ADD UNIQUE INDEX uk_basic_academical_culture_mapping_key (mapping_key),
    ADD INDEX idx_basic_academical_culture_source_policy (source_policy_key),
    ADD INDEX idx_basic_academical_culture_scope
        (college, major, start_entry_year, end_entry_year);

ALTER TABLE optional_mandatory_policy
    ADD COLUMN policy_key VARCHAR(255) NULL AFTER id,
    ADD COLUMN policy_category VARCHAR(50) NULL AFTER name;

UPDATE optional_mandatory_policy
SET policy_category = 'MAJOR_MANDATORY'
WHERE policy_category IS NULL;

ALTER TABLE optional_mandatory_policy
    MODIFY COLUMN policy_category VARCHAR(50) NOT NULL,
    ADD UNIQUE INDEX uk_optional_mandatory_policy_key (policy_key),
    ADD INDEX idx_optional_mandatory_policy_basic_scope
        (policy_category, major, start_entry_year, end_entry_year, status);
