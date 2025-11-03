package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLectureOffering;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureOfferingJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class MajorLectureOfferingMapper {

    /**
     * JPA 엔티티 → 도메인 객체 변환 (null-safe)
     */
    public MajorLectureOffering toDomain(MajorLectureOfferingJpaEntity entity) {
        if (entity == null) {
            return null; // nothing to map
        }

        final String lectureId = entity.getLectureId();
        final Integer grade = entity.getGrade();
        final Integer offeredSemester = entity.getOfferedSemester();

        // If key fields are missing, return null so the caller can ignore this record in recommendation.
        if (lectureId == null || offeredSemester == null || grade == null) {
            return null;
        }

        return MajorLectureOffering.ofCode(
                lectureId,
                grade,
                offeredSemester
        );
    }

    /**
     * 도메인 객체 → JPA 엔티티 변환
     */
    public MajorLectureOfferingJpaEntity toEntity(MajorLectureOffering domain) {
        if (domain == null) return null;

        return MajorLectureOfferingJpaEntity.builder()
                .lectureId(domain.getLectureId())
                .grade(domain.getGrade())
                .offeredSemester(domain.getOfferedSemesterCode())
                .build();
    }
}