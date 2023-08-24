package com.plzgraduate.myongjigraduatebe.auth.adaptor.in;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInRequest {

	private String userId;

	private String password;
}
