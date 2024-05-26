package com.plzgraduate.myongjigraduatebe.user.api.finduserinformation.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CompleteDivisionResponse {

	private final String majorType;

	private final String major;

	@Builder
	private CompleteDivisionResponse(String majorType, String major) {
		this.majorType = majorType;
		this.major = major;
	}

	public static CompleteDivisionResponse of(String majorType, String major) {
		return CompleteDivisionResponse.builder()
			.majorType(majorType)
			.major(major)
			.build();
	}
}
