package com.plzgraduate.myongjigraduatebe.auth.application.port.in.token;

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
