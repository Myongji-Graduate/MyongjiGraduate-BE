package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.FusionMajorLectureJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FusionMajorLectureRepository extends JpaRepository<FusionMajorLectureJpaEntity, Long> {

	@Query("select distinct f.lecture.id from FusionMajorLectureJpaEntity f where f.lecture.id in :ids")
	List<String> findLectureIdsIn(@Param("ids") List<String> ids);

	@Query("""
		select f from FusionMajorLectureJpaEntity f
		join fetch f.lecture
		join fetch f.policy p
		where p.fusionMajor = :fusionMajor
		  and p.startEntryYear <= :entryYear
		  and p.endEntryYear >= :entryYear
		""")
	List<FusionMajorLectureJpaEntity> findApplicable(
		@Param("fusionMajor") String fusionMajor,
		@Param("entryYear") int entryYear);

	@Query("""
		select f from FusionMajorLectureJpaEntity f
		join fetch f.lecture
		join fetch f.policy p
		where p.fusionMajor = :fusionMajor
		  and p.startEntryYear <= :entryYear
		  and p.endEntryYear >= :entryYear
		  and f.requirementArea = :requirementArea
		""")
	List<FusionMajorLectureJpaEntity> findApplicableByArea(
		@Param("fusionMajor") String fusionMajor,
		@Param("entryYear") int entryYear,
		@Param("requirementArea") String requirementArea);
}
