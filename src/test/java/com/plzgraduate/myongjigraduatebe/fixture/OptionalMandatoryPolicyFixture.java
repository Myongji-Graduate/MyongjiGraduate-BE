package com.plzgraduate.myongjigraduatebe.fixture;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.OptionalMandatoryPolicy.CandidateLecture;
import com.plzgraduate.myongjigraduatebe.graduation.domain.service.major.OptionalMandatoryMajorHandler;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import java.util.List;
import java.util.Map;

public final class OptionalMandatoryPolicyFixture {

	private static final Map<String, List<PolicyRange>> POLICIES = Map.of(
		"행정학전공", List.of(new PolicyRange(17, 24, policy("행정학전공", 2, 6,
			"HBA01109", "HBA01110", "HBA01111", "HBA01112", "HBA01113", "HBA01222"))),
		"경영학전공", List.of(new PolicyRange(8, 24, policy("경영학전공", 1, 3,
			"HBX01128", "HBX01127", "HBX01125", "HBY01103"))),
		"국제통상학전공", List.of(new PolicyRange(9, 24, policy("국제통상학전공", 4, 12,
			"HBX01104", "HBX01113", "HBX01106", "HBX01105", "HBX01147", "HBX01114", "HBX01143"))),
		"경영정보학과", List.of(
			new PolicyRange(19, 24, policy("경영정보학과", 2, 6,
				"HBX01104", "HBX01113", "HBX01106", "HBX01105", "HBX01147", "HBX01114", "HBX01143")),
			new PolicyRange(25, 99, policy("경영정보학과", 2, 6,
				"HBX01113", "HBX01106", "HBX01147")))
	);

	private OptionalMandatoryPolicyFixture() {
	}

	public static OptionalMandatoryMajorHandler handler() {
		return new OptionalMandatoryMajorHandler();
	}

	public static List<OptionalMandatoryPolicy> policies(String major, int entryYear) {
		return POLICIES.getOrDefault(major, List.of()).stream()
			.filter(range -> entryYear >= range.startEntryYear()
				&& entryYear <= range.endEntryYear())
			.map(PolicyRange::policy)
			.toList();
	}

	private static OptionalMandatoryPolicy policy(
		String major, int count, int credit, String... lectureIds) {
		List<CandidateLecture> candidates = java.util.Arrays.stream(lectureIds)
			.map(id -> new CandidateLecture(
				Lecture.of(id, id, 3, isRevoked(id), null), equivalenceKey(id)))
			.toList();
		return OptionalMandatoryPolicy.builder()
			.name(major + " 선택필수")
			.major(major)
			.requiredCount(count)
			.requiredCredit(credit)
			.candidateLectures(candidates)
			.build();
	}

	private static int isRevoked(String id) {
		return List.of("HBY01103", "HBX01105", "HBX01114").contains(id) ? 1 : 0;
	}

	private static String equivalenceKey(String id) {
		return switch (id) {
			case "HBY01103", "HBX01125" -> "BUSINESS_INFORMATION";
			case "HBX01105", "HBX01147" -> "FINANCIAL_MANAGEMENT";
			case "HBX01114", "HBX01143" -> "OPERATIONS_MANAGEMENT";
			default -> id;
		};
	}

	private record PolicyRange(
		int startEntryYear, int endEntryYear, OptionalMandatoryPolicy policy) {
	}
}
