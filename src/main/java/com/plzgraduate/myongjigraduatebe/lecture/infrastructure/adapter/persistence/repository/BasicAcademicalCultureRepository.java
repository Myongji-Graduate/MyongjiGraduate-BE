package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasicAcademicalCultureRepository extends
	JpaRepository<BasicAcademicalCultureLectureJpaEntity, Long> {

	@Query("select bac from BasicAcademicalCultureLectureJpaEntity bac join fetch bac.lectureJpaEntity where bac.college = :college")
	List<BasicAcademicalCultureLectureJpaEntity> findAllByCollege(@Param("college") String college);

	@Query("SELECT pb " +
		"FROM BasicAcademicalCultureLectureJpaEntity pb " +
		"JOIN BasicAcademicalCultureLectureJpaEntity db ON pb.lectureJpaEntity.id = db.lectureJpaEntity.id "
		+
		"JOIN TakenLectureJpaEntity tl ON pb.lectureJpaEntity.id = tl.lecture.id " +
		"WHERE tl.user.id = :userId " +
		"AND pb.college = :primary " +
		"AND db.college = :dual")
	List<BasicAcademicalCultureLectureJpaEntity> findAllDuplicatedTakenByCollages(
		@Param("userId") Long id,
		@Param("primary") String primaryMajorCollage, @Param("dual") String dualMajorCollage);
}
