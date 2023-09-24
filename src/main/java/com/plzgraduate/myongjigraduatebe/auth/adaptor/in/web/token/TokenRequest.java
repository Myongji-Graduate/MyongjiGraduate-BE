package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.token;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {
	private String refreshToken;

	@Builder
	private TokenRequest(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public static TokenCommand toCommand(String refreshToken) {
		return TokenCommand.builder()
			.refreshToken(refreshToken)
			.build();
	}
}
