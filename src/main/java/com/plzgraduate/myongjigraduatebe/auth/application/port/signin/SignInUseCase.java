package com.plzgraduate.myongjigraduatebe.auth.application.port.signin;

public interface SignInUseCase {
	SignInResponse signIn(SignInCommand signInCommand);
}
