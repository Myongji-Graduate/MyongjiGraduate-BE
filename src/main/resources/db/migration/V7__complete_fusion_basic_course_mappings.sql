UPDATE fusion_major_policy
SET basic_credit = CASE
    WHEN fusion_major = '응용데이터사이언스' THEN 3
    WHEN fusion_major = '인문ICT콘텐츠' THEN 9
    WHEN fusion_major = 'AI엔터프라이즈솔루션' THEN 3
    ELSE basic_credit
END
WHERE fusion_major IN (
    '응용데이터사이언스', '인문ICT콘텐츠', 'AI엔터프라이즈솔루션'
);

INSERT INTO fusion_major_lecture
    (policy_id, lecture_id, equivalence_key, requirement_area, mandatory)
SELECT p.id, mapping.lecture_id, mapping.equivalence_key, 'BASIC', mapping.mandatory
FROM fusion_major_policy p
JOIN (
    SELECT '응용데이터사이언스' fusion_major, 'KMD02194' lecture_id, 'AI_SW_BUSINESS' equivalence_key, 0 mandatory
    UNION ALL SELECT '응용데이터사이언스','KMF02113','FUSION_ENGINEERING_DESIGN',0
    UNION ALL SELECT '응용데이터사이언스','KMD02135','COPYRIGHT_SOFTWARE',0
    UNION ALL SELECT '응용데이터사이언스','KMI02112','COMPUTER_LOGIC',0
    UNION ALL SELECT '응용데이터사이언스','KMC02113','DESIGN_INTRO',0
    UNION ALL SELECT '응용데이터사이언스','KMD02193','MARKETING_AI_SW',0
    UNION ALL SELECT '응용데이터사이언스','KMF02112','STARTUP_MANAGEMENT',0
    UNION ALL SELECT '응용데이터사이언스','KME02106','STATISTICS_INTRO',0
    UNION ALL SELECT '응용데이터사이언스','KME02107','LINEAR_ALGEBRA',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02141','HUMANITIES_CONTENTS_INTRO',1
    UNION ALL SELECT '인문ICT콘텐츠','KMB02142','HUMANITIES_DATA',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02128','INFORMATION_LITERACY',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02122','WHAT_IS_HISTORY',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02125','CREATIVE_IMAGINATION',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02123','PHILOSOPHY_WALK',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02127','KOREAN_CULTURE_WRITING',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02120','ARAB_LANGUAGE_CULTURE',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02121','CHINESE_LANGUAGE_CULTURE',0
    UNION ALL SELECT '인문ICT콘텐츠','KMB02124','JAPANESE_LANGUAGE_CULTURE',0
) mapping ON mapping.fusion_major = p.fusion_major
JOIN lecture l ON l.id = mapping.lecture_id
WHERE NOT EXISTS (
    SELECT 1
    FROM fusion_major_lecture existing
    WHERE existing.policy_id = p.id
      AND existing.lecture_id = mapping.lecture_id
);
