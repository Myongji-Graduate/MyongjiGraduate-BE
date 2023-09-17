package com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.graduation.adpater.out.persistence.entity.GraduationRequirementJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirementJpaEntity, Long> {

	@Query("select gr from GraduationRequirementJpaEntity gr where gr.college = :college and gr.startEntryYear <= :entryYear and gr.endEntryYear >= :entryYear and gr.subMajorCredit = 0")
	GraduationRequirementJpaEntity findSingleMajorRequirementByUser(
		@Param("college") College college,
		@Param("entryYear") int startEntryYear
	);

	@Query("select gr from GraduationRequirementJpaEntity gr where gr.college = :college and gr.startEntryYear <= :entryYear and gr.endEntryYear >= :entryYear and gr.subMajorCredit > 0")
	GraduationRequirementJpaEntity findDualMajorRequirementByUser(
		@Param("college") College college,
		@Param("entryYear") int startEntryYear
	);
}
