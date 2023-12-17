package com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;

public interface SignInUseCase {
	TokenResponse signIn(SignInCommand signInCommand);
}
