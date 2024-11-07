package com.plzgraduate.myongjigraduatebe.user.api.signup.dto.request;

import com.plzgraduate.myongjigraduatebe.user.application.usecase.signup.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {

	@NotBlank(message = "아이디를 입력해주세요.")
	@Size(min = 6, max = 20, message = "INVALIDATED_AUTHID_TYPE")
	@Schema(name = "authId", example = "plzgraduate")
	private String authId;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "^(?=.*[!@#$%^&*])(?=.*[a-zA-Z0-9]).{8,20}$", message = "INVALIDATED_PASSWORD_TYPE")
	@Schema(name = "password", example = "Plz1231343!")
	private String password;

	@NotBlank(message = "비밀번호를 입력해주세요.")
	@Pattern(regexp = "\\d{8}", message = "INVALIDATED_STUDENT_NUMBER_TYPE")
	@Schema(name = "studentNumber", example = "60202000")
	private String studentNumber;

	@NotBlank(message = "영어레벨을 입력해주세요.")
	@Schema(name = "engLv", example = "ENG34")
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
