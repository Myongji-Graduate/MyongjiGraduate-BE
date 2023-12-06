package com.plzgraduate.myongjigraduatebe.auth.application.port.in.token;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.AccessTokenResponse;

public interface TokenUseCase {
	AccessTokenResponse createNewToken(TokenCommand tokenCommand);
}
