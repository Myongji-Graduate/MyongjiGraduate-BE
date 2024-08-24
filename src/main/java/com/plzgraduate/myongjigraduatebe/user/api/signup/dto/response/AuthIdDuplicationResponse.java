package com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class AuthIdDuplicationResponse {
	@Schema(name = "authId", example = "plzgraduate")
	private final String authId;
	@Schema(name = "notDuplicated", example = "true")
	private final boolean notDuplicated;

	@Builder
	private AuthIdDuplicationResponse(String authId, boolean notDuplicated) {
		this.authId = authId;
		this.notDuplicated = notDuplicated;
	}
}
