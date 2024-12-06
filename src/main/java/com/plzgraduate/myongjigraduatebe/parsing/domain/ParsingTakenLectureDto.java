package com.plzgraduate.myongjigraduatebe.parsing.domain;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ParsingTakenLectureDto {

	private final String lectureCode;
	private final int year;
	private final Semester semester;

	@Builder
	private ParsingTakenLectureDto(String lectureCode, int year, Semester semester) {
		this.lectureCode = lectureCode;
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
}
