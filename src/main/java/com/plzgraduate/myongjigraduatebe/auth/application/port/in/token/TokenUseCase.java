package com.plzgraduate.myongjigraduatebe.auth.application.port.in.token;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;

public interface TokenUseCase {
	TokenResponse createNewToken(TokenCommand tokenCommand);
}
