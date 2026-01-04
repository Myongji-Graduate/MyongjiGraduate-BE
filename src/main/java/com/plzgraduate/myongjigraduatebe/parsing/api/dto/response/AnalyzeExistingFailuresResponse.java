package com.plzgraduate.myongjigraduatebe.parsing.api.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AnalyzeExistingFailuresResponse {

	private final int analyzedCount;

	private AnalyzeExistingFailuresResponse(int analyzedCount) {
		this.analyzedCount = analyzedCount;
	}

	public static AnalyzeExistingFailuresResponse of(int analyzedCount) {
		return AnalyzeExistingFailuresResponse.builder()
			.analyzedCount(analyzedCount)
			.build();
	}
}

