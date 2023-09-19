package com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin;

public interface SignInUseCase {
	SignInResponse signIn(SignInCommand signInCommand);
}
