package com.plzgraduate.myongjigraduatebe.auth.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenResponse {
	private String accessToken;

	@Builder
	private AccessTokenResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public static AccessTokenResponse from(String accessToken) {
		return AccessTokenResponse
			.builder()
			.accessToken(accessToken)
			.build();
	}
}
