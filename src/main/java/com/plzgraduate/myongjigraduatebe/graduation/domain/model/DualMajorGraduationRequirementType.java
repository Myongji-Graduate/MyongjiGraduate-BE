package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DualMajorGraduationRequirementType {

	HUMANITIES("인문대", 36, 12),
	SOCIAL_SCIENCE("사회과학대", 36, 12),
	BUSINESS("경영대", 45, 6),
	LAW("법대", 36, 9),
	ICT("ICT융합대", 42, 18);

	private final String collageName;
	private final int majorCredit;
	private final int basicAcademicalCultureCredit;

	public static DualMajorGraduationRequirementType findBelongingDualMajorGraduationRequirementType(
		String collageName) {
		return Arrays.stream(DualMajorGraduationRequirementType.values())
			.filter(dualMajorGraduationRequirementType ->
				dualMajorGraduationRequirementType.getCollageName()
					.equals(collageName))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}
}
