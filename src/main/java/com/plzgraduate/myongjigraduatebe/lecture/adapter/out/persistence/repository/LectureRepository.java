package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.LectureJpaEntity;

public interface LectureRepository extends JpaRepository<LectureJpaEntity, Long> {

	List<LectureJpaEntity> findByLectureCodeIn(List<String> lectureCodes);
}
