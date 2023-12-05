package com.plzgraduate.myongjigraduatebe.auth.adaptor.out.persistence;

import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.auth.application.port.out.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.application.port.out.SaveRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;
import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;

import lombok.RequiredArgsConstructor;


@PersistenceAdapter
@RequiredArgsConstructor
public class RefreshTokenAdaptor implements FindRefreshTokenPort, SaveRefreshTokenPort {

	//private final RefreshTokenRepository refreshTokenRepository;
	//private final RefreshTokenMapper refreshTokenMapper;

	@Override
	public Optional<RefreshToken> findByRefreshToken(String refreshToken) {
		/**
		Optional<RefreshTokenRedisEntity> refreshTokenRedisEntity = refreshTokenRepository.findByRefreshToken(refreshToken);
		if(refreshTokenRedisEntity.isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(refreshTokenMapper.mapToDomainEntity(refreshTokenRedisEntity.get()));
		 **/
		return Optional.empty();
	}

	@Override
	public void saveRefreshToken(RefreshToken refreshToken) {
		/**
		refreshTokenRepository.save(refreshTokenMapper.mapToJpaEntity(refreshToken));
		 **/
	}
}
