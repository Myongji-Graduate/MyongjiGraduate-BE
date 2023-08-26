package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.plzgraduate.myongjigraduatebe.auth.application.port.command.SignInCommand;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	@NotBlank(message = "아아디를 입력해주세요.")
	@Size(min = 6, max = 20, message = "아이디는 8자에서 20자 사이여야합니다.")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야합니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$", message = "비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")
	private String password;

	public SignInCommand toCommand() {
		return SignInCommand
			.builder()
			.authId(authId)
			.password(password)
			.build();
	}
}
