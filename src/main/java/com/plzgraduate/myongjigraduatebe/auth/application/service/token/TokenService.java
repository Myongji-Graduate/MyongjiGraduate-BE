package com.plzgraduate.myongjigraduatebe.auth.application.service.token;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.out.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class TokenService implements TokenUseCase {

	private final TokenProvider tokenProvider;

	private final FindRefreshTokenPort findRefreshTokenPort;
	@Override
	public AccessTokenResponse createNewToken(TokenCommand tokenCommand) {
		Long userId = findByRefreshToken(tokenCommand.getRefreshToken());
		String accessToken = tokenProvider.generateToken(userId);
		return AccessTokenResponse.from(accessToken);
	}

	private Long findByRefreshToken(String refreshToken) {
		return findRefreshTokenPort.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new IllegalArgumentException("유효하지 않은 토큰입니다."));
	}
}
