package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.signup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.signup.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 6, max = 20, message = "아이디는 6자에서 20자 사이여야합니다.")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Size(min = 8, max = 20, message = "비밀번호는 8자에서 20자 사이여야합니다.")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).+$", message = "비밀번호는 문자, 숫자, 기호가 1개 이상 포함되어야합니다.")
	private String password;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "\\d{8}", message = "학번은 8자리 숫자여야 합니다.")
	private String studentNumber;

	@NotBlank(message = "영어레벨을 입력해주세요.")
	private String engLv;

	@Builder
	private SignUpRequest(String authId, String password, String studentNumber, String engLv) {
		this.authId = authId;
		this.password = password;
		this.studentNumber = studentNumber;
		this.engLv = engLv;
	}

	public SignUpCommand toCommand() {
		return SignUpCommand.builder()
			.authId(authId)
			.password(password)
			.studentNumber(studentNumber)
			.engLv(EnglishLevel.valueOf(engLv))
			.build();
	}
}
