package com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {

	private final Long id;
	private final String code;
	private final String name;
	private final int credit;

	@Builder
	private LectureResponse(Long id, String code, String name, int credit) {
		this.id = id;
		this.code = code;
		this.name = name;
		this.credit = credit;
	}

	public static LectureResponse from(Lecture lecture) {
		return LectureResponse.builder()
			.id(lecture.getId())
			.code(lecture.getLectureCode())
			.name(lecture.getName())
			.credit(lecture.getCredit()).build();
	}
}
