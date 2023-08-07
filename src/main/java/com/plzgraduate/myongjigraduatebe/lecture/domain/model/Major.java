package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Major {

	private final Lecture lecture;
	private final String department;
	private final int isMandatory;
	private final int startYear;

	@Builder
	private Major(Lecture lecture, String department, int isMandatory, int startYear) {
		this.lecture = lecture;
		this.department = department;
		this.isMandatory = isMandatory;
		this.startYear = startYear;
	}

}
