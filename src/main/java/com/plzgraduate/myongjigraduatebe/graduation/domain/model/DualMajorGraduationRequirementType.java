package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DualMajorGraduationRequirementType {

	HUMANITIES("인문대", 36, 36),
	SOCIAL_SCIENCE("사회과학대", 36, 36),
	BUSINESS("경영대", 45, 45),
	LAW("법대", 36, 36),
	ICT("ICT융합대", 42, 42);

	private final String name;
	private final int originMajorCredit;
	private final int dualMajorCredit;

	public static DualMajorGraduationRequirementType findBelongingDualMajorGraduationRequirementType(String name) {
		return Arrays.stream(DualMajorGraduationRequirementType.values())
			.filter(dualMajorGraduationRequirementType -> dualMajorGraduationRequirementType.getName().equals(name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("소속 단과대가 존재하지 않습니다."));
	}
}
