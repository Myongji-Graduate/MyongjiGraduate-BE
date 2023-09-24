package com.plzgraduate.myongjigraduatebe.auth.application.port.in;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {

	private String accessToken;
	private String refreshToken;

	@Builder
	private TokenResponse(String accessToken, String refreshToken) {
		this.accessToken = accessToken;
		this.refreshToken = refreshToken;
	}

	public static TokenResponse from(String accessToken, String refreshToken) {
		return TokenResponse
			.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
