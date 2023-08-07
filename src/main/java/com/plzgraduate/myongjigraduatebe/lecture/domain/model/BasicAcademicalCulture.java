package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicAcademicalCulture {

	private Lecture lecture;
	private String department;

	@Builder
	private BasicAcademicalCulture(Lecture lecture, String department) {
		this.lecture = lecture;
		this.department = department;
	}

	public static BasicAcademicalCulture of(Lecture lecture, String department) {
		return BasicAcademicalCulture.builder()
			.lecture(lecture)
			.department(department)
			.build();
	}
}
