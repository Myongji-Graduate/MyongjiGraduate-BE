package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class MajorLecture {

	private final Lecture lecture;
	private final String major;
	private final int appliedStartEntryYear;
	private final int appliedEndEntryYear;
	private int isMandatory;

	@Builder
	private MajorLecture(Lecture lecture, String major, int isMandatory, int appliedStartEntryYear,
		int appliedEndEntryYear) {
		this.lecture = lecture;
		this.major = major;
		this.isMandatory = isMandatory;
		this.appliedStartEntryYear = appliedStartEntryYear;
		this.appliedEndEntryYear = appliedEndEntryYear;
	}

	public static MajorLecture of(Lecture lecture, String major, int isMandatory,
		int appliedStartEntryYear,
		int appliedEndEntryYear) {
		return MajorLecture.builder()
			.lecture(lecture)
			.major(major)
			.isMandatory(isMandatory)
			.appliedStartEntryYear(appliedStartEntryYear)
			.appliedEndEntryYear(appliedEndEntryYear)
			.build();
	}

	public void changeMandatoryToElectiveByEntryYearRange(int entryYear) {
		if (checkMandatoryByEntryYear(entryYear)) {
			isMandatory = 0;
		}
	}

	private boolean checkMandatoryByEntryYear(int entryYear) {
		return isMandatory == 1 && !(entryYear >= appliedStartEntryYear
			&& entryYear <= appliedEndEntryYear);
	}

}
