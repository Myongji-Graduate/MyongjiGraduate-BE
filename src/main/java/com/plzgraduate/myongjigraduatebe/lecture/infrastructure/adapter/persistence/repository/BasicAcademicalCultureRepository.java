package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.BasicAcademicalCultureLectureJpaEntity;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BasicAcademicalCultureRepository extends
	JpaRepository<BasicAcademicalCultureLectureJpaEntity, Long> {

	@Query("select bac from BasicAcademicalCultureLectureJpaEntity bac " +
		"join fetch bac.lectureJpaEntity where " +
		"(bac.major = :major or (bac.major is null and bac.college = :college " +
			"and not exists (" +
				"select scoped.id from BasicAcademicalCultureLectureJpaEntity scoped " +
				"where scoped.lectureJpaEntity.id = bac.lectureJpaEntity.id " +
				"and scoped.major = :major " +
				"and (scoped.startEntryYear is null or scoped.startEntryYear <= :entryYear) " +
				"and (scoped.endEntryYear is null or scoped.endEntryYear >= :entryYear)" +
			") and (" +
			"bac.mappingKey is not null or not exists (" +
				"select managed.id from BasicAcademicalCultureLectureJpaEntity managed " +
				"where managed.mappingKey is not null " +
				"and managed.major is null " +
				"and managed.college = :college " +
				"and (managed.startEntryYear is null or managed.startEntryYear <= :entryYear) " +
				"and (managed.endEntryYear is null or managed.endEntryYear >= :entryYear)" +
			")" +
		"))) " +
		"and (bac.startEntryYear is null or bac.startEntryYear <= :entryYear) " +
		"and (bac.endEntryYear is null or bac.endEntryYear >= :entryYear) " +
		"and (bac.mappingKey is not null or bac.major is not null or not exists (" +
			"select legacy.id from BasicAcademicalCultureLectureJpaEntity legacy " +
			"where legacy.mappingKey is not null " +
			"and legacy.major is null " +
			"and legacy.college = :college " +
			"and (legacy.startEntryYear is null or legacy.startEntryYear <= :entryYear) " +
			"and (legacy.endEntryYear is null or legacy.endEntryYear >= :entryYear)" +
		"))")
	List<BasicAcademicalCultureLectureJpaEntity> findAllApplicable(
		@Param("college") String college,
		@Param("major") String major,
		@Param("entryYear") int entryYear
	);

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

    @Query("select b.lectureJpaEntity.id " +
            "from BasicAcademicalCultureLectureJpaEntity b " +
            "where b.lectureJpaEntity.id in :ids")
    List<String> findIdsByLectureIdIn(@Param("ids") List<String> ids);

    @Query("select b.lectureJpaEntity.id " +
            "from BasicAcademicalCultureLectureJpaEntity b " +
            "where b.lectureJpaEntity.id in :ids " +
            "and b.college in :colleges")
    List<String> findIdsByLectureIdInAndCollegeIn(
            @Param("ids") List<String> lectureIds,
            @Param("colleges") Set<String> colleges
    );

    boolean existsByLectureJpaEntity_Id(String lectureId);
}
