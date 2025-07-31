package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingTakenLectureDto {

	private final String lectureCode;
	private final int year;
	private final Semester semester;
	private final String lectureName;

	@Builder
	private ParsingTakenLectureDto(
		String lectureCode,
		int year,
		Semester semester,
		String lectureName
	) {
		this.lectureCode = lectureCode;
		this.year = year;
		this.semester = semester;
		this.lectureName = lectureName;
	}

	public static ParsingTakenLectureDto of(String lectureCode, int year, Semester semester, String lectureName) {
		return ParsingTakenLectureDto.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.lectureName(lectureName)
			.build();
	}
}
