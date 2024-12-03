package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {

	@Schema(name = "id", example = "KMA02103")
	private final String id;
	@Schema(name = "name", example = "종교와과학")
	private final String name;
	@Schema(name = "credit", example = "2")
	private final int credit;

	@Builder
	private LectureResponse(String id, String name, int credit) {
		this.id = id;
		this.name = name;
		this.credit = credit;
	}

	public static LectureResponse from(Lecture lecture) {
		return LectureResponse.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.build();
	}
}
