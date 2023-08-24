package com.plzgraduate.myongjigraduatebe.user.adaptor.in.web;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
	private String userId;

	private String password;

	private String studentNumber;

	private String engLv;
}
