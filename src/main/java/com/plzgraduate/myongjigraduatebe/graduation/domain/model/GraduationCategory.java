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
	BASIC_ACADEMICAL_CULTURE("학문기초교양"),
	MAJOR("전공"),
	SUB_MAJOR("부전공"),
	NORMAL_CULTURE("일반교양"),
	FREE_ELECTIVE("자유선택");

	private final String name;

	public static GraduationCategory of(String name) {
		return Arrays.stream(GraduationCategory.values())
			.filter(category -> Objects.equals(category.getName(), name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
	}
}
