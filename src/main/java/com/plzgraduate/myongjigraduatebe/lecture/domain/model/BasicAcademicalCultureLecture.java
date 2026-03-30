package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicAcademicalCultureLecture implements Serializable {
	private static final long serialVersionUID = 1L;

	private final Lecture lecture;
	private final String college;

	@Builder
	private BasicAcademicalCultureLecture(Lecture lecture, String college) {
		this.lecture = lecture;
		this.college = college;
	}

	public static BasicAcademicalCultureLecture of(Lecture lecture, String college) {
		return BasicAcademicalCultureLecture.builder()
			.lecture(lecture)
			.college(college)
			.build();
	}
}
