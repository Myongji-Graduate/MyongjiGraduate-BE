package com.plzgraduate.myongjigraduatebe.auth.domain;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RefreshToken {

	private final String refreshToken;

	private final Long userId;

	@Builder
	private RefreshToken(String refreshToken, Long userId) {
		this.refreshToken = refreshToken;
		this.userId = userId;
	}

	public static RefreshToken createToken(Long userId) {
		return RefreshToken.builder()
			.refreshToken(UUID.randomUUID().toString())
			.userId(userId)
			.build();
	}
}
