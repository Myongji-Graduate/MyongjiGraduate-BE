package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.token;

import javax.validation.constraints.NotBlank;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {

	@NotBlank(message = "unexpected token")
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
