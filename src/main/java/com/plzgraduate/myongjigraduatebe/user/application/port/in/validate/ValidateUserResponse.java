package com.plzgraduate.myongjigraduatebe.user.application.port.in.validate;

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
