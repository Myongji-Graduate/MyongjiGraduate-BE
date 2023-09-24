package com.plzgraduate.myongjigraduatebe.auth.adaptor.out.persistence;

import org.springframework.stereotype.Component;

import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;

@Component
class RefreshTokenMapper {
	RefreshTokenRedisEntity mapToJpaEntity(RefreshToken refreshToken) {
		return RefreshTokenRedisEntity.builder()
			.refreshToken(refreshToken.getRefreshToken())
			.userId(refreshToken.getUserId())
			.build();
	}

	RefreshToken mapToDomainEntity(RefreshTokenRedisEntity refreshTokenRedisEntity) {
		return RefreshToken.builder()
			.refreshToken(refreshTokenRedisEntity.getRefreshToken())
			.userId(refreshTokenRedisEntity.getUserId())
			.build();
	}
}
