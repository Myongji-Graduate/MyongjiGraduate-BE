package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureOfferingJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MajorLectureOfferingJpaRepository extends JpaRepository<MajorLectureOfferingJpaEntity, String> {
}
