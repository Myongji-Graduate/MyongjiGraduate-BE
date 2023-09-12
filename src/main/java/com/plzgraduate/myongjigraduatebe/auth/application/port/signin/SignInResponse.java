package com.plzgraduate.myongjigraduatebe.auth.application.port.signin;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInResponse {

	private String accessToken;

	@Builder
	private SignInResponse(String accessToken) {
		this.accessToken = accessToken;
	}

	public static SignInResponse from(String accessToken) {
		return SignInResponse
			.builder()
			.accessToken(accessToken)
			.build();
	}
}
