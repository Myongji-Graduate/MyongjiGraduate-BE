package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DualMajorGraduationRequirementType {

	HUMANITIES("인문대", List.of(), 36, 12, 16, 99),
	SOCIAL_SCIENCE("사회과학대", List.of(), 36, 12, 16, 99),
	BUSINESS_16_24("경영대", List.of(), 45, 6, 16, 24),
	BUSINESS_25_99("경영대", List.of("경영학전공", "경영정보학과"), 45, 9, 25, 99),
	BUSINESS_GLOBAL_25_99("경영대", List.of("글로벌비즈니스학전공"), 36, 6, 25, 99),
	LAW("법대", List.of(), 36, 9, 16, 99),
	ICT("ICT융합대", List.of(), 42, 18, 16, 99);

	private final String collageName;
	private final List<String> majors;
	private final int majorCredit;
	private final int basicAcademicalCultureCredit;
	private final int startEntryYear;
	private final int endEntryYear;

	public static DualMajorGraduationRequirementType findBelongingDualMajorGraduationRequirementType(
		String collageName, String major, int entryYear) {
		return Arrays.stream(DualMajorGraduationRequirementType.values())
			.filter(dualMajorGraduationRequirementType ->
				dualMajorGraduationRequirementType.getCollageName()
					.equals(collageName))
			.filter(requirement -> requirement.getStartEntryYear() <= entryYear
				&& entryYear <= requirement.getEndEntryYear())
			.filter(requirement -> requirement.getMajors().isEmpty()
				|| requirement.getMajors().contains(major))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}
}
