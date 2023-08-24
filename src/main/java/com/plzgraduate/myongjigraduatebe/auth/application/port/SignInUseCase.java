package com.plzgraduate.myongjigraduatebe.auth.application.port;

import com.plzgraduate.myongjigraduatebe.auth.adaptor.in.SignInRequest;

public interface SignInUseCase {
	String signIn(SignInRequest signInRequest);
}
