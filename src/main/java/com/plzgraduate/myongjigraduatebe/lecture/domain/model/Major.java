package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Major {

	private final Lecture lecture;
	private final String department;
	private int isMandatory;
	private final int appliedStartEntryYear;
	private final int appliedEndEntryYear;

	@Builder
	private Major(Lecture lecture, String department, int isMandatory, int appliedStartEntryYear, int appliedEndEntryYear) {
		this.lecture = lecture;
		this.department = department;
		this.isMandatory = isMandatory;
		this.appliedStartEntryYear = appliedStartEntryYear;
		this.appliedEndEntryYear = appliedEndEntryYear;
	}

	public static Major of(Lecture lecture, String department, int isMandatory, int appliedStartEntryYear, int appliedEndEntryYear) {
		return Major.builder()
			.lecture(lecture)
			.department(department)
			.isMandatory(isMandatory)
			.appliedStartEntryYear(appliedStartEntryYear)
			.appliedEndEntryYear(appliedEndEntryYear)
			.build();
	}

	public void changeMandatoryToElectiveByEntryYearRange(int entryYear) {
		if(checkMandatoryByEntryYear(entryYear)) {
			isMandatory = 0;
		}
	}

	private boolean checkMandatoryByEntryYear(int entryYear) {
		return isMandatory==1 && !(entryYear >= appliedStartEntryYear && entryYear <= appliedEndEntryYear);
	}

}
