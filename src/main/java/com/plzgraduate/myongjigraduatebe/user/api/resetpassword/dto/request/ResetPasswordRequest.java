package com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResetPasswordRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	@Schema(name = "authId", example = "plzgraduate")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*])(?=.*[a-zA-Z0-9]).{8,20}$", message = "INVALIDATED_PASSWORD_TPYE")
	@Schema(name = "newPassword", example = "Plz1231343!?")
	private String newPassword;

	@NotBlank(message = "비밀번호 확인을 입력해주세요.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*])(?=.*[a-zA-Z0-9]).{8,20}$", message = "INVALIDATED_PASSWORD_TPYE")
	@Schema(name = "passwordCheck", example = "Plz1231343!?")
	private String passwordCheck;

	@Builder
	private ResetPasswordRequest(String authId, String newPassword, String passwordCheck) {
		this.authId = authId;
		this.newPassword = newPassword;
		this.passwordCheck = passwordCheck;
	}

}
