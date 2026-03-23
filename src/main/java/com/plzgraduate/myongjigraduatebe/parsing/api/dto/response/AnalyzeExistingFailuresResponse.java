package com.plzgraduate.myongjigraduatebe.parsing.api.dto.response;

import lombok.Getter;

@Getter
public class AnalyzeExistingFailuresResponse {

	private final int analyzedCount;

	private AnalyzeExistingFailuresResponse(int analyzedCount) {
		this.analyzedCount = analyzedCount;
	}

	public static AnalyzeExistingFailuresResponse of(int analyzedCount) {
		return new AnalyzeExistingFailuresResponse(analyzedCount);
	}
}