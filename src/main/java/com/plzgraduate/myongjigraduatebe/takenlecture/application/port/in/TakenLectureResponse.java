package com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in;

import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureResponse {

	private final Long id;

	private final String year;

	private final String semester;

	private final String lectureCode;

	private final String lectureName;

	private final int credit;

	@Builder
	private TakenLectureResponse(Long id, String year, String semester, String lectureCode, String lectureName,
		int credit) {
		this.id = id;
		this.year = year;
		this.semester = semester;
		this.lectureCode = lectureCode;
		this.lectureName = lectureName;
		this.credit = credit;
	}

	public static TakenLectureResponse from(TakenLecture takenLecture) {
		return TakenLectureResponse.builder()
			.id(takenLecture.getId())
			.year(takenLecture.getYear() == 2099 ? "CUSTOM" : String.valueOf(takenLecture.getYear()))
			.semester(takenLecture.getSemester() == null ? "CUSTOM" : String.valueOf(takenLecture.getYear()))
			.lectureCode(takenLecture.getLecture().getLectureCode())
			.lectureName(takenLecture.getLecture().getName())
			.credit(takenLecture.getLecture().getCredit())
			.build();
	}
}
