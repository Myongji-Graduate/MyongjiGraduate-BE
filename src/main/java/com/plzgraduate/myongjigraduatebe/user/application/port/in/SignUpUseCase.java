package com.plzgraduate.myongjigraduatebe.user.application.port.in;

import com.plzgraduate.myongjigraduatebe.user.application.port.in.command.SignUpCommand;

public interface SignUpUseCase {
	void signUp(SignUpCommand signUpCommand);
}
