package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonCultureCategory {
	CHRISTIAN_A("기독교", 4, List.of(16, 17, 18, 19)),
	CHRISTIAN_B("기독교",4, List.of(20, 21, 22, 23)),
	EXPRESSION("사고와 표현", 3, List.of(16, 17, 18, 19, 20 ,21, 22, 23)),
	ENGLISH("영어", 6, List.of(116, 17, 18, 19, 20 ,21, 22, 23)),
	CAREER("진로", 2, List.of(18, 19, 20, 21, 22)),
	DIGITAL_LITERACY("진로와디지털리터러시", 2,  List.of(23));

	private final String name;
	private final int totalCredit;
	private final List<Integer> containsEntryYears;

	public boolean isContainsEntryYear(int entryYear) {
		return containsEntryYears.contains(entryYear);
	}

}
