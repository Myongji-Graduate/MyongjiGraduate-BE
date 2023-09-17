package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {
	private final Long id;
	private final String lectureCode;
	private final String name;
	private final int credit;
	private final boolean isRevoked;

	@Builder
	private LectureResponse(Long id, String lectureCode, String name, int credit, boolean isRevoked) {
		this.id = id;
		this.lectureCode = lectureCode;
		this.name = name;
		this.credit = credit;
		this.isRevoked = isRevoked;
	}

	public static LectureResponse of(Lecture lecture) {
		return LectureResponse.builder()
			.id(lecture.getId())
			.lectureCode(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked() == 0)
			.build();
	}
}
