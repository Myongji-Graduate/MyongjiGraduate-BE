package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Set;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StudentCategory {
	NORMAL(Set.of()),
	CHANGE_MAJOR(Set.of("")),
	DOUBLE_MAJOR(Set.of("복수전공")),
	ASSOCIATED_MAJOR(Set.of("연계전공")),
	DOUBLE_ASSOCIATED(Set.of("연계전공", "복수전공"));

	private final Set<String> categories;

	public static StudentCategory from(Set<String> categories) {
		return StudentCategory.NORMAL;
	}
}
