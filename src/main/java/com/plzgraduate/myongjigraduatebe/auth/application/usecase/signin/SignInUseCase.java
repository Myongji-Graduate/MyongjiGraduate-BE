package com.plzgraduate.myongjigraduatebe.auth.application.usecase.signin;

import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.response.TokenResponse;

public interface SignInUseCase {
	TokenResponse signIn(String authId, String password);
}
