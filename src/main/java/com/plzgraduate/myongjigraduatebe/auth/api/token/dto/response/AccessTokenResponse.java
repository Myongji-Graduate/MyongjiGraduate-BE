package com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccessTokenResponse {

	@Schema(name = "accessToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
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
