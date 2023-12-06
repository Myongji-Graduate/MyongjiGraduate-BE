package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInCommand;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	@NotBlank(message = "아아디를 입력해주세요.")
	@Size(min = 6, max = 20, message = "아이디는 6자에서 20자 사이여야합니다.")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*])(?=.*[a-zA-Z0-9]).{8,20}$", message = "비밀번호는 특수문자를 포함한 8자에서 20자 사이여야합니다.")
	private String password;

	@Builder
	private SignInRequest(String authId, String password) {
		this.authId = authId;
		this.password = password;
	}

	public SignInCommand toCommand() {
		return SignInCommand
			.builder()
			.authId(authId)
			.password(password)
			.build();
	}
}
