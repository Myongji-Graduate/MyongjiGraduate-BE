package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.PolicyCategory;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity.OptionalMandatoryPolicyJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity.OptionalMandatoryPolicyJpaEntity.PolicyStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OptionalMandatoryPolicyRepository
	extends JpaRepository<OptionalMandatoryPolicyJpaEntity, Long> {

	@Query("select distinct p from OptionalMandatoryPolicyJpaEntity p "
		+ "left join fetch p.lectures pl "
		+ "left join fetch pl.lecture "
		+ "where p.major in :majors "
		+ "and p.startEntryYear <= :entryYear and p.endEntryYear >= :entryYear "
		+ "and (p.majorType is null or p.majorType = :majorType) "
		+ "and p.policyCategory = :category and p.status = :status")
	List<OptionalMandatoryPolicyJpaEntity> findActivePolicies(
		@Param("majors") Collection<String> majors,
		@Param("entryYear") int entryYear,
		@Param("majorType") MajorType majorType,
		@Param("category") PolicyCategory category,
		@Param("status") PolicyStatus status);

	@Query("select distinct p from OptionalMandatoryPolicyJpaEntity p "
		+ "left join fetch p.lectures pl "
		+ "left join fetch pl.lecture "
		+ "where p.major in :majors "
		+ "and p.startEntryYear <= :entryYear and p.endEntryYear >= :entryYear "
		+ "and p.policyCategory = :category and p.status = :status")
	List<OptionalMandatoryPolicyJpaEntity> findActiveBasicPolicies(
		@Param("majors") Collection<String> majors,
		@Param("entryYear") int entryYear,
		@Param("category") PolicyCategory category,
		@Param("status") PolicyStatus status);
}
