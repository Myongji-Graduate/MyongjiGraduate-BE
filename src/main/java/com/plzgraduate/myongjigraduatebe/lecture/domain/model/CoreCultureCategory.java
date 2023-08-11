package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CoreCultureCategory {

	HISTORY_PHILOSOPHY("역사와철학", 3),
	SOCIETY_COMMUNITY("사회와공동체", 3),
	CULTURE_ART("문화와예술", 3),
	SCIENCE_TECHNOLOGY("과학과기술", 3);

	private final String name;
	private final int totalCredit;

}
