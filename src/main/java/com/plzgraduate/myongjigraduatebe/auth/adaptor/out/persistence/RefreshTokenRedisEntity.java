package com.plzgraduate.myongjigraduatebe.auth.adaptor.out.persistence;

import javax.persistence.Id;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshTokenRedisEntity {
	@Id
	private String refreshToken;
	private Long userId;

	@Builder
	private RefreshTokenRedisEntity(String refreshToken, Long userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}

	public static RefreshTokenRedisEntity create(String refreshToken, Long userId) {
		return RefreshTokenRedisEntity.builder()
			.refreshToken(refreshToken)
			.userId(userId)
			.build();
	}
}
