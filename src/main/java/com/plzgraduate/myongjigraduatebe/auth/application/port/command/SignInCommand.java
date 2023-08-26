package com.plzgraduate.myongjigraduatebe.auth.application.port.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInCommand {

	private String authId;

	private String password;

	@Builder
	private SignInCommand(String authId, String password) {
		this.authId = authId;
		this.password = password;
	}
}
