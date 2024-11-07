package com.plzgraduate.myongjigraduatebe.user.application.usecase.signup;

import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpCommand {

	private String authId;

	private String password;

	private String studentNumber;

	private EnglishLevel engLv;

	@Builder
	private SignUpCommand(String authId, String password, String studentNumber,
		EnglishLevel engLv) {
		this.authId = authId;
		this.password = password;
		this.studentNumber = studentNumber;
		this.engLv = engLv;
	}
}
