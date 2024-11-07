package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<LectureJpaEntity, Long> {

	List<LectureJpaEntity> findByLectureCodeIn(List<String> lectureCodes);

	List<LectureJpaEntity> findByIdIn(List<Long> ids);
}
