package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingTakenLectureDto {

	private final String lectureCode;
	private final String lectureName;
	private final int credit;
	private final int year;
	private final Semester semester;

	@Builder
	private ParsingTakenLectureDto(
		String lectureCode,
		String lectureName,
		int credit,
		int year,
		Semester semester
	) {
		this.lectureCode = lectureCode;
		this.lectureName = lectureName;
		this.credit = credit;
		this.year = year;
		this.semester = semester;
	}

	public static ParsingTakenLectureDto of(String lectureCode, int year, Semester semester) {
		return ParsingTakenLectureDto.builder()
			.lectureCode(lectureCode)
			.year(year)
			.semester(semester)
			.build();
	}

	public static ParsingTakenLectureDto of(
		String lectureCode,
		String lectureName,
		int credit,
		int year,
		Semester semester
	) {
		return ParsingTakenLectureDto.builder()
			.lectureCode(lectureCode)
			.lectureName(lectureName)
			.credit(credit)
			.year(year)
			.semester(semester)
			.build();
	}
}
