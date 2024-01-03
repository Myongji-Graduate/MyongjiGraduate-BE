package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.signin;

import javax.validation.constraints.NotBlank;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInCommand;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	@NotBlank(message = "아아디를 입력해주세요.")
	@Schema(name = "authId", example = "plzgraduate")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Schema(name = "password", example = "Plz1231343!")
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
