package com.plzgraduate.myongjigraduatebe.auth.adaptor.out.persistence;

import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.auth.application.port.out.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;

import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class RefreshTokenAdaptor implements FindRefreshTokenPort {
	private final RefreshTokenRepository refreshTokenRepository;

	@Override
	public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
		return null;
	}
}
