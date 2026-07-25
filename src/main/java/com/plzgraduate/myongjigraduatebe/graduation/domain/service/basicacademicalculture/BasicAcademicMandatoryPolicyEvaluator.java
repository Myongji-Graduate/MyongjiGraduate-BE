package com.plzgraduate.myongjigraduatebe.graduation.domain.service.basicacademicalculture;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BasicAcademicMandatoryPolicyEvaluator {

	private BasicAcademicMandatoryPolicyEvaluator() {
	}

	public static Evaluation evaluate(
		List<OptionalMandatoryPolicy> policies,
		TakenLectureInventory inventory
	) {
		boolean satisfied = true;
		Set<Lecture> remainingCandidates = new LinkedHashSet<>();
		Set<String> takenRecognitionCodes = inventory.getTakenLectures().stream()
			.map(taken -> taken.getLecture().getRecognitionCode())
			.collect(Collectors.toSet());

		for (OptionalMandatoryPolicy policy : policies) {
			Map<String, CandidateLecture> candidatesByRecognitionCode =
				policy.getCandidateLectures().stream()
					.collect(Collectors.toMap(
						candidate -> candidate.lecture().getRecognitionCode(),
						Function.identity(),
						(first, ignored) -> first
					));
			Set<String> takenKeys = takenRecognitionCodes.stream()
				.map(candidatesByRecognitionCode::get)
				.filter(candidate -> candidate != null)
				.map(CandidateLecture::equivalenceKey)
				.collect(Collectors.toSet());
			if (takenKeys.size() >= policy.getRequiredCount()) {
				continue;
			}
			satisfied = false;
			policy.getCandidateLectures().stream()
				.filter(candidate -> !takenKeys.contains(candidate.equivalenceKey()))
				.map(CandidateLecture::lecture)
				.forEach(remainingCandidates::add);
		}
		return new Evaluation(satisfied, remainingCandidates);
	}

	public record Evaluation(boolean satisfied, Set<Lecture> remainingCandidates) {
	}
}
