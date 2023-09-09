package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicAcademicalCulture {

	private Lecture lecture;
	private String college;

	@Builder
	private BasicAcademicalCulture(Lecture lecture, String college) {
		this.lecture = lecture;
		this.college = college;
	}

	public static BasicAcademicalCulture of(Lecture lecture, String college) {
		return BasicAcademicalCulture.builder()
			.lecture(lecture)
			.college(college)
			.build();
	}
}
