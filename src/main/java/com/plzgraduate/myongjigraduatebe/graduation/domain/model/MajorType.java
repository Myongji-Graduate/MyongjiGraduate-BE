package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import lombok.Getter;

@Getter
public enum MajorType {
	PRIMARY,
	DUAL,
	SUB;

	public static MajorType from(GraduationCategory graduationCategory) {
		if (graduationCategory == GraduationCategory.PRIMARY_MANDATORY_MAJOR
			|| graduationCategory == GraduationCategory.PRIMARY_ELECTIVE_MAJOR
			|| graduationCategory == GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE) {
			return PRIMARY;
		} else if (graduationCategory == GraduationCategory.DUAL_MANDATORY_MAJOR
			|| graduationCategory == GraduationCategory.DUAL_ELECTIVE_MAJOR
			|| graduationCategory == GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE) {
			return DUAL;
		}
		return SUB;
	}
}
