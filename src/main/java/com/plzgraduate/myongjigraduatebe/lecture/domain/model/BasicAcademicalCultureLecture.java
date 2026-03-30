package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class BasicAcademicalCultureLecture {

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
