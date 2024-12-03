package com.plzgraduate.myongjigraduatebe.graduation.api.dto.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapelResultResponse {

	@Schema(name = "takenCount", example = "4")
	private final int takenCount;
	@Schema(name = "completed", example = "true")
	private final boolean completed;

	@Builder
	private ChapelResultResponse(int takenCount, boolean completed) {
		this.takenCount = takenCount;
		this.completed = completed;
	}

	public static ChapelResultResponse from(ChapelResult chapelResult) {
		return ChapelResultResponse.builder()
			.takenCount(chapelResult.getTakenCount())
			.completed(chapelResult.isCompleted())
			.build();
	}
}
