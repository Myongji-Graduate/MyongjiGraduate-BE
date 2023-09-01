package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LectureRepository extends JpaRepository<LectureJpaEntity, Long> {

	List<LectureJpaEntity> findByLectureCodeIn(List<String> lectureCodes);
}
