package com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordCommand {

	private String authId;

	private String newPassword;

	private String passwordCheck;

	@Builder
	private ResetPasswordCommand(String authId, String newPassword, String passwordCheck) {
		this.authId = authId;
		this.newPassword = newPassword;
		this.passwordCheck = passwordCheck;
	}
}
