package com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.entity.InvalidTakenLectureLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTakenLectureLogJpaRepository extends JpaRepository<InvalidTakenLectureLogEntity, Long> {
}
