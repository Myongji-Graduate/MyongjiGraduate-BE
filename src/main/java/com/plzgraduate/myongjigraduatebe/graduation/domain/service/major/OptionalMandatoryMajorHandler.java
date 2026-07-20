package com.plzgraduate.myongjigraduatebe.graduation.domain.service.major;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.MajorType;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class OptionalMandatoryMajorHandler implements MandatoryMajorSpecialCaseHandler {

	@Override
	public Optional<MandatorySpecialCaseInformation> evaluate(User user,
		MajorType majorType, TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures,
		List<OptionalMandatoryPolicy> policies) {
		if (policies.isEmpty()) {
			return Optional.empty();
		}
		boolean completed = true;
		int removedMandatoryTotalCredit = 0;
		for (OptionalMandatoryPolicy policy : policies) {
			boolean policyCompleted = applyPolicy(
				takenLectureInventory, mandatoryLectures, electiveLectures, policy);
			completed &= policyCompleted;
			if (!policyCompleted) {
				removedMandatoryTotalCredit += calculateRemovableCredit(policy);
			}
		}
		return Optional.of(MandatorySpecialCaseInformation.of(
			completed, removedMandatoryTotalCredit));
	}

	private boolean applyPolicy(TakenLectureInventory takenLectureInventory,
		Set<Lecture> mandatoryLectures, Set<Lecture> electiveLectures,
		OptionalMandatoryPolicy policy) {
		Map<String, CandidateLecture> candidatesById = policy.getCandidateLectures().stream()
			.collect(Collectors.toMap(candidate -> candidate.lecture().getId(), Function.identity()));
		Set<Lecture> policyMandatoryLectures = mandatoryLectures.stream()
			.filter(lecture -> candidatesById.containsKey(lecture.getId()))
			.collect(Collectors.toSet());
		Set<String> policyMandatoryIds = policyMandatoryLectures.stream()
			.map(Lecture::getId)
			.collect(Collectors.toSet());
		Map<String, String> representativeLectureIdsByKey = new LinkedHashMap<>();
		takenLectureInventory.getTakenLectures().stream()
			.filter(takenLecture -> policyMandatoryIds.contains(takenLecture.getLecture().getId()))
			.sorted(Comparator
				.comparing(TakenLecture::getYear, Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(TakenLecture::getSemester,
					Comparator.nullsLast(Comparator.naturalOrder()))
				.thenComparing(takenLecture -> takenLecture.getLecture().getId()))
			.forEach(takenLecture -> {
				Lecture lecture = takenLecture.getLecture();
				String equivalenceKey = candidatesById.get(lecture.getId()).equivalenceKey();
				representativeLectureIdsByKey.putIfAbsent(equivalenceKey, lecture.getId());
			});
		Set<String> takenEquivalenceKeys = new LinkedHashSet<>(
			representativeLectureIdsByKey.keySet());

		if (takenEquivalenceKeys.size() >= policy.getRequiredCount()) {
			Set<String> retainedMandatoryLectureIds = representativeLectureIdsByKey.values().stream()
				.limit(policy.getRequiredCount())
				.collect(Collectors.toSet());
			Set<Lecture> remainingMandatoryLectures = policyMandatoryLectures.stream()
				.filter(lecture -> !retainedMandatoryLectureIds.contains(lecture.getId()))
				.collect(Collectors.toSet());
			electiveLectures.addAll(remainingMandatoryLectures);
			mandatoryLectures.removeAll(remainingMandatoryLectures);
		}
		return takenEquivalenceKeys.size() >= policy.getRequiredCount();
	}

	private int calculateRemovableCredit(OptionalMandatoryPolicy policy) {
		int candidateCredit = policy.getCandidateLectures().stream()
			.map(CandidateLecture::lecture)
			.filter(lecture -> lecture.getIsRevoked() == 0)
			.mapToInt(Lecture::getCredit)
			.sum();
		return Math.max(0, candidateCredit - policy.getRequiredCredit());
	}
}
