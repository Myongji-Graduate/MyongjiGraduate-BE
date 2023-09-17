package com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.lecture.adapter.out.persistence.entity.BasicAcademicalCultureLectureJpaEntity;

public interface BasicAcademicalCultureRepository extends JpaRepository<BasicAcademicalCultureLectureJpaEntity, Long> {

	@Query("select bac from BasicAcademicalCultureLectureJpaEntity bac join fetch bac.lectureJpaEntity where bac.college = :college")
	List<BasicAcademicalCultureLectureJpaEntity> findAllByCollege(@Param("college") String college);
}
