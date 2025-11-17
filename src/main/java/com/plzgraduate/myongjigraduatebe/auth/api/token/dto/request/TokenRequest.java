package com.plzgraduate.myongjigraduatebe.auth.api.token.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
}
