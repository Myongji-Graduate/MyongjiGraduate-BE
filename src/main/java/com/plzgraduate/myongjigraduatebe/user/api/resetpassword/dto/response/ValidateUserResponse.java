package com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidateUserResponse {

	@Schema(name = "passedUserValidation", example = "true")
	private final boolean passedUserValidation;

	@Builder
	private ValidateUserResponse(boolean passedUserValidation) {
		this.passedUserValidation = passedUserValidation;
	}
}
