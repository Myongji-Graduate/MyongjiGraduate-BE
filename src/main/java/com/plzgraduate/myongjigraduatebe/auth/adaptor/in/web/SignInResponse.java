package com.plzgraduate.myongjigraduatebe.auth.adaptor.in.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class SignInResponse {

	private String accessToken;

	public static SignInResponse from(String accessToken) {
		return new SignInResponse(accessToken);
	}
}
