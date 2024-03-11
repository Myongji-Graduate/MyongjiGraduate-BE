package com.plzgraduate.myongjigraduatebe.auth.application.usecase.token;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;

public interface TokenUseCase {
	AccessTokenResponse generateNewToken(String refreshToken);
}
