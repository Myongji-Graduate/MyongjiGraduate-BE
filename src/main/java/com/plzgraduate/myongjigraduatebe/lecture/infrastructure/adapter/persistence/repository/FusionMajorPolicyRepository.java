package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.FusionMajorPolicyJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FusionMajorPolicyRepository extends JpaRepository<FusionMajorPolicyJpaEntity, Long> {

	Optional<FusionMajorPolicyJpaEntity> findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
		String fusionMajor, int startEntryYear, int endEntryYear);
}
