package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum GraduationCategory {

	COMMON_CULTURE("공통교양"),
	CORE_CULTURE("핵심교양"),
	PRIMARY_MANDATORY_MAJOR("주전공필수"),
	PRIMARY_ELECTIVE_MAJOR("주전공선택"),
	DUAL_MANDATORY_MAJOR("복수전공필수"),
	DUAL_ELECTIVE_MAJOR("복수전공선택"),
	SUB_MAJOR("부전공"),
	PRIMARY_BASIC_ACADEMICAL_CULTURE("주학문기초교양"),
	DUAL_BASIC_ACADEMICAL_CULTURE("복수학문기초교양"),
	ASSOCIATED_MANDATORY_MAJOR("연계전공필수"),
	ASSOCIATED_ELECTIVE_MAJOR("연계전공선택"),
	ASSOCIATED_MANDATORY_CULTURE("연계전공교양필수"),
	ASSOCIATED_ELECTIVE_CULTURE("연계전공교양선택"),
	TRANSFER_COMBINED_CULTURE("편입교양"),
	TRANSFER_CHRISTIAN("편입기독교"),
	NORMAL_CULTURE("일반교양"),
	FREE_ELECTIVE("자유선택"),
	CHAPEL("채플");

	private final String name;

	public static GraduationCategory of(String name) {
		return Arrays.stream(GraduationCategory.values())
			.filter(category -> Objects.equals(category.getName(), name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
	}

	public boolean checkMandatoryIfSeperatedByMandatoryAndElective() {
		return this == PRIMARY_MANDATORY_MAJOR || this == DUAL_MANDATORY_MAJOR;
	}

	@Override
	public String toString() {
		return "GraduationCategory{" +
			"name='" + name + '\'' +
			'}';
	}
}
