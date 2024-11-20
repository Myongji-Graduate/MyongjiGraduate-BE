package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.MajorLectureJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MajorLectureRepository extends JpaRepository<MajorLectureJpaEntity, Long> {

	@Query("select m from MajorLectureJpaEntity m join fetch m.lectureJpaEntity where m.major = :major or m.major = '실습'")
	List<MajorLectureJpaEntity> findAllByMajor(@Param("major") String major);
}
