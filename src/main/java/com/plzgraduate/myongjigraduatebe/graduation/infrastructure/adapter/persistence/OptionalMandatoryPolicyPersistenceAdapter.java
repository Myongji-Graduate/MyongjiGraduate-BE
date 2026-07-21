package com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.graduation.application.port.FindOptionalMandatoryPolicyPort;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity.OptionalMandatoryPolicyJpaEntity;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.entity.OptionalMandatoryPolicyJpaEntity.PolicyStatus;
import com.plzgraduate.myongjigraduatebe.graduation.infrastructure.adapter.persistence.repository.OptionalMandatoryPolicyRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class OptionalMandatoryPolicyPersistenceAdapter implements FindOptionalMandatoryPolicyPort {

	private final OptionalMandatoryPolicyRepository repository;
	private final LectureMapper lectureMapper;

	@Override
	public List<OptionalMandatoryPolicy> findActivePolicies(
		String major, int entryYear, MajorType majorType) {
		return repository.findActivePolicies(major, entryYear, majorType, PolicyStatus.ACTIVE).stream()
			.map(this::mapToDomain)
			.toList();
	}

	private OptionalMandatoryPolicy mapToDomain(OptionalMandatoryPolicyJpaEntity entity) {
		List<CandidateLecture> candidates = entity.getLectures().stream()
			.map(candidate -> new CandidateLecture(
				lectureMapper.mapToLectureModel(candidate.getLecture()),
				candidate.getEquivalenceKey()))
			.toList();
		return OptionalMandatoryPolicy.builder()
			.id(entity.getId())
			.name(entity.getName())
			.major(entity.getMajor())
			.requiredCount(entity.getRequiredCount())
			.requiredCredit(entity.getRequiredCredit())
			.candidateLectures(candidates)
			.build();
	}
}
