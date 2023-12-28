package com.plzgraduate.myongjigraduatebe.lecture.application.port.in.search;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {
	@Schema(name = "id", example = "18")
	private final Long id;
	@Schema(name = "lectureCode", example = "KMA02137")
	private final String lectureCode;
	@Schema(name = "name", example = "4차산업혁명시대의진로선택")
	private final String name;
	@Schema(name = "credit", example = "2")
	private final int credit;
	@Schema(name = "isRevoked", example = "false")
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
