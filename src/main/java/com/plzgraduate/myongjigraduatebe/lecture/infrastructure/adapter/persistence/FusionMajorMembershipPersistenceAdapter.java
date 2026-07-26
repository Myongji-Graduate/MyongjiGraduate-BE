package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.mapper.LectureMapper;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.FusionMajorLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.FusionMajorPolicyRepository;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class FusionMajorMembershipPersistenceAdapter implements FusionMajorMembershipPort {

	private final FusionMajorLectureRepository lectureRepository;
	private final FusionMajorPolicyRepository policyRepository;
	private final LectureMapper lectureMapper;

	@Override
	public Set<String> findFusionMajorLectureIds(List<String> lectureIds) {
		if (lectureIds == null || lectureIds.isEmpty()) {
			return Set.of();
		}
		return new HashSet<>(lectureRepository.findLectureIdsIn(lectureIds));
	}

	@Override
	public Set<Lecture> findLectures(String fusionMajor, int entryYear) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return Set.of();
		}
		return lectureRepository.findApplicable(normalize(fusionMajor), entryYear).stream()
			.map(entity -> lectureMapper.mapToLectureModel(entity.getLecture()))
			.collect(Collectors.toSet());
	}

	@Override
	public Set<Lecture> findLectures(
		String fusionMajor, int entryYear, String requirementArea
	) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return Set.of();
		}
		return lectureRepository.findApplicableByArea(
				normalize(fusionMajor), entryYear, requirementArea).stream()
			.map(entity -> lectureMapper.mapToLectureModel(entity.getLecture()))
			.collect(Collectors.toSet());
	}

	@Override
	public Set<String> findMandatoryLectureIds(
		String fusionMajor, int entryYear, String requirementArea
	) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return Set.of();
		}
		return lectureRepository.findApplicableByArea(
				normalize(fusionMajor), entryYear, requirementArea).stream()
			.filter(entity -> entity.getMandatory() == 1)
			.map(entity -> entity.getLecture().getId())
			.collect(Collectors.toSet());
	}

	@Override
	public int findRequiredCredit(String fusionMajor, int entryYear) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return 0;
		}
		return policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				normalize(fusionMajor), entryYear, entryYear)
			.map(policy -> policy.getRequiredCredit())
			.orElse(0);
	}

	public int findRequiredCredit(
		String fusionMajor, int entryYear, String primaryMajor
	) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return 0;
		}
		boolean business = isBusinessMajor(primaryMajor, entryYear);
		return policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				normalize(fusionMajor), entryYear, entryYear)
			.map(policy -> business && policy.getBusinessRequiredCredit() != null
				? policy.getBusinessRequiredCredit() : policy.getRequiredCredit())
			.orElse(0);
	}

	@Override
	public int findRequiredBasicCredit(
		String fusionMajor, int entryYear, String primaryMajor
	) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return 0;
		}
		boolean business = isBusinessMajor(primaryMajor, entryYear);
		return policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				normalize(fusionMajor), entryYear, entryYear)
			.map(policy -> business && policy.getBusinessBasicCredit() != null
				? policy.getBusinessBasicCredit() : policy.getBasicCredit())
			.orElse(0);
	}

	@Override
	public int findRequiredPrimaryMajorCredit(
		String fusionMajor, int entryYear, String primaryMajor
	) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return 0;
		}
		boolean business = isBusinessMajor(primaryMajor, entryYear);
		return policyRepository
			.findFirstByFusionMajorAndStartEntryYearLessThanEqualAndEndEntryYearGreaterThanEqual(
				normalize(fusionMajor), entryYear, entryYear)
			.map(policy -> business && policy.getBusinessPrimaryMajorCredit() != null
				? policy.getBusinessPrimaryMajorCredit() : policy.getPrimaryMajorCredit())
			.orElse(0);
	}

	@Override
	public Map<String, String> findEquivalenceKeys(String fusionMajor, int entryYear) {
		if (fusionMajor == null || fusionMajor.isBlank()) {
			return Map.of();
		}
		return lectureRepository.findApplicable(normalize(fusionMajor), entryYear).stream()
			.collect(Collectors.toMap(
				entity -> entity.getLecture().getId(),
				entity -> entity.getEquivalenceKey(),
				(left, right) -> left));
	}

	private String normalize(String fusionMajor) {
		String normalized = fusionMajor.trim().replaceFirst("전공$", "");
		return switch (normalized.replace(" ", "")) {
			case "프로세스자동화경영" -> "AI엔터프라이즈솔루션";
			case "사회복지학" -> "휴먼복지행정";
			default -> normalized;
		};
	}

	private boolean isBusinessMajor(String primaryMajor, int entryYear) {
		if (primaryMajor == null) {
			return false;
		}
		try {
			College college = College.findBelongingCollege(primaryMajor, entryYear);
			return college == College.BUSINESS || college == College.BUSINESS_NEW;
		} catch (IllegalArgumentException ignored) {
			return false;
		}
	}
}
