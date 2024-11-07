package com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
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
}
