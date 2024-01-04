package com.plzgraduate.myongjigraduatebe.graduation.application.port.in.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {

	@Schema(name = "id", example = "6")
	private final Long id;
	@Schema(name = "code", example = "KMA02103")
	private final String code;
	@Schema(name = "name", example = "종교와과학")
	private final String name;
	@Schema(name = "credit", example = "2")
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
