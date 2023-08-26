package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web.request;

import com.plzgraduate.myongjigraduatebe.auth.application.port.command.SignInCommand;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	private String authId;

	private String password;

	public SignInCommand toCommand() {
		return SignInCommand
			.builder()
			.authId(authId)
			.password(password)
			.build();
	}


}
