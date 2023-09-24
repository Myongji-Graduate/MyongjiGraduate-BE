package com.plzgraduate.myongjigraduatebe.auth.application.service.token;

import org.springframework.security.core.Authentication;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenUseCase;
import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationToken;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class TokenService implements TokenUseCase {
	private final TokenProvider tokenProvider;

	private final RefreshTokenService refreshTokenService;
	@Override
	public TokenResponse createNewToken(TokenCommand tokenCommand) {
		RefreshToken refreshToken = refreshTokenService.findByRefreshToken(tokenCommand.getRefreshToken());
		String accessToken = tokenProvider.generateToken(refreshToken.getUserId());
		return TokenResponse.from(accessToken, refreshToken.getRefreshToken());
	}
}
