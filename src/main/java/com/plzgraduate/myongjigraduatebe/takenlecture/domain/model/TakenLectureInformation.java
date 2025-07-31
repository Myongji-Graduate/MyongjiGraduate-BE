package com.plzgraduate.myongjigraduatebe.takenlecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureInformation {

	private final String lectureCode;
	private final int year;
	private final Semester semester;
	private final String lectureName;

	@Builder
	private TakenLectureInformation(String lectureCode, int year, Semester semester, String lectureName) {
		this.lectureCode = lectureCode;
		this.year = year;
		this.semester = semester;
        this.lectureName = lectureName;
    }

	public static TakenLectureInformation createTakenLectureInformation(String lectureCode,
		int year, Semester semester, String lectureName) {
		return TakenLectureInformation.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.lectureName(lectureName)
			.build();
	}
}
