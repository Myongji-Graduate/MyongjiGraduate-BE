package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LectureResponse {

	@Schema(name = "id", example = "KMA02137")
	private final String id;
	@Schema(name = "name", example = "4차산업혁명시대의진로선택")
	private final String name;
	@Schema(name = "credit", example = "2")
	private final int credit;
	@Schema(name = "isRevoked", example = "false")
	private final boolean isRevoked;

	@Builder
	private LectureResponse(String id, String name, int credit, boolean isRevoked) {
		this.id = id;
		this.name = name;
		this.credit = credit;
		this.isRevoked = isRevoked;
	}

	public static LectureResponse of(Lecture lecture) {
		return LectureResponse.builder()
			.id(lecture.getId())
			.name(lecture.getName())
			.credit(lecture.getCredit())
			.isRevoked(lecture.getIsRevoked() != 0)
			.build();
	}
}
