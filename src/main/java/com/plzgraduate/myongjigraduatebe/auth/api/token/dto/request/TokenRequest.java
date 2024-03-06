package com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request;

import javax.validation.constraints.NotBlank;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenRequest {

	@NotBlank(message = "unexpected token")
	@Schema(name = "refreshToken", example = "7f734e1b-669d-430e-ac78-270e3863db50")
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
