package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.Semester;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;
import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicAcademicalCultureLecture {

	private final Lecture lecture;
	private final String college;
	private final String major;
	private final Integer startEntryYear;
	private final Integer endEntryYear;
	private final Integer startTakenYear;
	private final Semester startTakenSemester;
	private final Integer endTakenYear;
	private final Semester endTakenSemester;

	@Builder
	private BasicAcademicalCultureLecture(
		Lecture lecture,
		String college,
		String major,
		Integer startEntryYear,
		Integer endEntryYear,
		Integer startTakenYear,
		Semester startTakenSemester,
		Integer endTakenYear,
		Semester endTakenSemester
	) {
		this.lecture = lecture;
		this.college = college;
		this.major = major;
		this.startEntryYear = startEntryYear;
		this.endEntryYear = endEntryYear;
		this.startTakenYear = startTakenYear;
		this.startTakenSemester = startTakenSemester;
		this.endTakenYear = endTakenYear;
		this.endTakenSemester = endTakenSemester;
	}

	public static BasicAcademicalCultureLecture of(Lecture lecture, String college) {
		return BasicAcademicalCultureLecture.builder()
			.lecture(lecture)
			.college(college)
			.build();
	}

	public static BasicAcademicalCultureLecture fromTaken(
		Lecture lecture, String college, int year, Semester semester
	) {
		return BasicAcademicalCultureLecture.builder()
			.lecture(lecture)
			.college(college)
			.startTakenYear(year)
			.startTakenSemester(semester)
			.build();
	}

	public boolean recognizes(TakenLecture takenLecture) {
		if (!lecture.getRecognitionCode()
			.equals(takenLecture.getLecture().getRecognitionCode())) {
			return false;
		}
		return recognizesAt(takenLecture.getYear(), takenLecture.getSemester());
	}

	public boolean recognizesAt(Integer year, Semester semester) {
		return isOnOrAfter(year, semester, startTakenYear, startTakenSemester)
			&& isOnOrBefore(year, semester, endTakenYear, endTakenSemester);
	}

	private boolean isOnOrAfter(
		Integer takenYear,
		Semester takenSemester,
		Integer boundaryYear,
		Semester boundarySemester
	) {
		if (boundaryYear == null) {
			return true;
		}
		if (takenYear == null || takenYear < boundaryYear) {
			return false;
		}
		return takenYear > boundaryYear || boundarySemester == null
			|| takenSemester != null
			&& takenSemester.getValue() >= boundarySemester.getValue();
	}

	private boolean isOnOrBefore(
		Integer takenYear,
		Semester takenSemester,
		Integer boundaryYear,
		Semester boundarySemester
	) {
		if (boundaryYear == null) {
			return true;
		}
		if (takenYear == null || takenYear > boundaryYear) {
			return false;
		}
		return takenYear < boundaryYear || boundarySemester == null
			|| takenSemester != null
			&& takenSemester.getValue() <= boundarySemester.getValue();
	}
}
