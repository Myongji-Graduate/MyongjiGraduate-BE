package com.plzgraduate.myongjigraduatebe.auth.infrastructure.adapter.repository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.plzgraduate.myongjigraduatebe.auth.application.port.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.application.port.SaveRefreshTokenPort;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InMemoryTokenRepository implements FindRefreshTokenPort, SaveRefreshTokenPort {

	private static final Cache<String, Long> TOKEN_REPOSITORY = CacheBuilder.newBuilder()
		.expireAfterWrite(15, TimeUnit.DAYS)
		.build();

	@Override
	public void saveRefreshToken(String refreshToken, Long userId) {
		TOKEN_REPOSITORY.put(refreshToken, userId);
	}

	@Override
	public Optional<Long> findByRefreshToken(String refreshToken) {
		return Optional.ofNullable(TOKEN_REPOSITORY.getIfPresent(refreshToken));
	}
}
