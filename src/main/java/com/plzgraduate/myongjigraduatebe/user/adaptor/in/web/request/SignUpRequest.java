package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.request;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.SignUpCommand;
import com.plzgraduate.myongjigraduatebe.user.domain.model.EnglishLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
	private String authId;

	private String password;

	private String studentNumber;

	private String engLv;

	public SignUpCommand toCommand() {
		return SignUpCommand.builder()
			.authId(authId)
			.password(password)
			.studentNumber(studentNumber)
			.engLv(EnglishLevel.valueOf(engLv))
			.build();
	}
}
