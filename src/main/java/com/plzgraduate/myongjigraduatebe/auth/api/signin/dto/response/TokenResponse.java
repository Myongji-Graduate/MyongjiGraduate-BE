package com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TokenResponse {

	@Schema(name = "accessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
	private String accessToken;

	@Schema(name = "refreshToken", example = "7f734e1b-669d-430e-ac78-270e3863db50")
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
