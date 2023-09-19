package com.plzgraduate.myongjigraduatebe.auth.application.service.signin;

import com.plzgraduate.myongjigraduatebe.auth.application.port.out.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class RefreshTokenService {

	private final FindRefreshTokenPort findRefreshTokenPort;

	public RefreshToken findByRefreshToken(String refreshToken) {
		return findRefreshTokenPort.findByRefreshToken(refreshToken)
			.orElseThrow(() -> new IllegalArgumentException(""));
	}
}
