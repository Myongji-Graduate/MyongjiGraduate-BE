package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonCultureCategory {
	CHRISTIAN_A("기독교", 4, 16, 19),
	CHRISTIAN_B("기독교", 4, 20, 99),
	EXPRESSION("사고와 표현", 3, 16, 99),
	ENGLISH("영어", 6, 16, 99),
	CAREER("진로", 2, 18, 22),
	DIGITAL_LITERACY("진로와디지털리터러시", 2, 23, 99),
	KOREAN("한국어", 6, 16, 24);

	private final String name;
	private final int totalCredit;
	private final int startEntryYear;
	private final int endEntryYear;

	public boolean isContainsEntryYear(int entryYear) {
		return startEntryYear <= entryYear && entryYear <= endEntryYear;
	}
}
