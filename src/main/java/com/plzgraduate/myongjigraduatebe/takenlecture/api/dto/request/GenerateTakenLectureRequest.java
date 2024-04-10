package com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateTakenLectureRequest {

	@Schema(name = "lectureId", example = "103")
	private Long lectureId;

	@Builder
	private GenerateTakenLectureRequest(Long lectureId) {
		this.lectureId = lectureId;
	}
}
