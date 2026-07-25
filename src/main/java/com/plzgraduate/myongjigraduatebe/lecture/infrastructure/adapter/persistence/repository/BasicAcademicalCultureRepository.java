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
		"(bac.major = :major or (bac.major is null and bac.college = :college and not exists (" +
			"select specific.id from BasicAcademicalCultureLectureJpaEntity specific " +
			"where specific.major = :major " +
			"and (specific.startEntryYear is null or specific.startEntryYear <= :entryYear) " +
			"and (specific.endEntryYear is null or specific.endEntryYear >= :entryYear)" +
		"))) " +
		"and (bac.startEntryYear is null or bac.startEntryYear <= :entryYear) " +
		"and (bac.endEntryYear is null or bac.endEntryYear >= :entryYear) " +
		"and (bac.mappingKey is not null or not exists (" +
			"select managed.id from BasicAcademicalCultureLectureJpaEntity managed " +
			"where managed.mappingKey is not null " +
			"and (managed.major = :major or (managed.major is null and managed.college = :college)) " +
			"and (managed.startEntryYear is null or managed.startEntryYear <= :entryYear) " +
			"and (managed.endEntryYear is null or managed.endEntryYear >= :entryYear)" +
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
