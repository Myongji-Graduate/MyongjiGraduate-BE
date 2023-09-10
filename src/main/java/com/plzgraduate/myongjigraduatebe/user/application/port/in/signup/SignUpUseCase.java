package com.plzgraduate.myongjigraduatebe.user.application.port.in.signup;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.signup.SignUpCommand;

public interface SignUpUseCase {
	void signUp(SignUpCommand signUpCommand);
}
