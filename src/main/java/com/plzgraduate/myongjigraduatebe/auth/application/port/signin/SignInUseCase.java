package com.plzgraduate.myongjigraduatebe.auth.application.port.signin;

import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInResponse;

public interface SignInUseCase {
	SignInResponse signIn(SignInCommand signInCommand);
}
