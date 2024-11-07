package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import java.util.Arrays;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Semester {
	FIRST("1학기"),
	SUMMER("하계계절"),
	SECOND("2학기"),
	WINTER("동계계절");

	private final String name;

	public static Semester of(String name) {
		return Arrays.stream(Semester.values())
			.filter(semester -> Objects.equals(semester.getName(), name))
			.findFirst()
			.orElseThrow(() -> new IllegalArgumentException("해당 학기를 찾을 수 없습니다."));
	}
}
