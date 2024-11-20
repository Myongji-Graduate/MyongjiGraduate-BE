package com.plzgraduate.myongjigraduatebe.takenlecture.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class GenerateCustomizedTakenLectureRequest {

	@Schema(name = "lectureId", example = "KMA02137")
	private String lectureId;

	@Builder
	private GenerateCustomizedTakenLectureRequest(String lectureId) {
		this.lectureId = lectureId;
	}
}
