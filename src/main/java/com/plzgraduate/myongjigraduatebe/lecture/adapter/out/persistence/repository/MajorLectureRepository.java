package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.MajorLectureJpaEntity;

public interface MajorLectureRepository extends JpaRepository<MajorLectureJpaEntity, Long> {

	@Query("select m from MajorLectureJpaEntity m join fetch m.lectureJpaEntity where m.major = :major")
	List<MajorLectureJpaEntity> findAllByMajor(@Param("major") String major);
}
