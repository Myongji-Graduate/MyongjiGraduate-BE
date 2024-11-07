package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureInformation {

	private final String lectureCode;
	private final int year;
	private final Semester semester;

	@Builder
	private TakenLectureInformation(String lectureCode, int year, Semester semester) {
		this.lectureCode = lectureCode;
		this.year = year;
		this.semester = semester;
	}

	public static TakenLectureInformation createTakenLectureInformation(String lectureCode,
		int year, Semester semester) {
		return TakenLectureInformation.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.build();
	}
}
