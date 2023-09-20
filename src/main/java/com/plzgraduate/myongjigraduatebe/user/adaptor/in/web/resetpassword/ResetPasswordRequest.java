package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.resetpassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.resetpassword.ResetPasswordCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야합니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$", message = "비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")
	private String newPassword;

	@NotBlank(message = "비밀번호 확인을 입력해주세요.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야합니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$", message = "비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")
	private String passwordCheck;

	@Builder
	private ResetPasswordRequest(String authId, String newPassword, String passwordCheck) {
		this.authId = authId;
		this.newPassword = newPassword;
		this.passwordCheck = passwordCheck;
	}

	public ResetPasswordCommand toCommand() {
		return ResetPasswordCommand.builder()
			.authId(authId)
			.newPassword(newPassword)
			.passwordCheck(passwordCheck).build();
	}
}
