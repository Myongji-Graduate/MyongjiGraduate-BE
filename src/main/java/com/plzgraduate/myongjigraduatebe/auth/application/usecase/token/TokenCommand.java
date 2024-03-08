package com.plzgraduate.myongjigraduatebe.auth.application.usecase.token;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class TokenCommand {
	private final String refreshToken;

	@Builder
	private TokenCommand(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
