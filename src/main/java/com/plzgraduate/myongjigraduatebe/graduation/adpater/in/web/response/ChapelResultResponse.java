package com.plzgraduate.myongjigraduatebe.graduation.adpater.in.web.response;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChapelResultResponse {

	private final int totalCredit = 4;
	private final int takenCount;
	private final boolean completed;

	@Builder
	private ChapelResultResponse(int takenCount, boolean completed) {
		this.takenCount = takenCount;
		this.completed = completed;
	}

	public static ChapelResultResponse from(ChapelResult chapelResult) {
		return ChapelResultResponse.builder()
			.takenCount(chapelResult.getTakenCount())
			.completed(chapelResult.isCompleted()).build();
	}
}