package com.plzgraduate.myongjigraduatebe.auth.application.port;

import com.plzgraduate.myongjigraduatebe.auth.application.port.command.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.response.SignInResponse;

public interface SignInUseCase {
	SignInResponse signIn(SignInCommand signInCommand);
}
