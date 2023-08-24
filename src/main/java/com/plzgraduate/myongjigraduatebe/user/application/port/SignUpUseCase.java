package com.plzgraduate.myongjigraduatebe.user.application.port;

import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.SignUpRequest;

public interface SignUpUseCase {
	void signUp(SignUpRequest signUpRequest);
}
