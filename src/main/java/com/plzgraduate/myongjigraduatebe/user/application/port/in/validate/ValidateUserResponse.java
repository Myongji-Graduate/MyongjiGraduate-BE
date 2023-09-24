package com.plzgraduate.myongjigraduatebe.user.application.port.in.validate;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ValidateUserResponse {

	private final boolean passedUserValidation;

	@Builder
	private ValidateUserResponse(boolean passedUserValidation) {
		this.passedUserValidation = passedUserValidation;
	}
}
