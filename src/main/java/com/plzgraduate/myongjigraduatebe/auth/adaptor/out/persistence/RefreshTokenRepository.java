package com.plzgraduate.myongjigraduatebe.auth.adaptor.out.persistence;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {
	private final RedisTemplate<String, Long> redisTemplate;

	public void save(RefreshTokenRedisEntity refreshToken) {
		ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(refreshToken.getRefreshToken(), refreshToken.getUserId());
		redisTemplate.expire(refreshToken.getRefreshToken(), 60L, TimeUnit.SECONDS);
	}

	public Optional<RefreshTokenRedisEntity> findByRefreshToken(final String refreshToken) {
		ValueOperations<String, Long> valueOperations = redisTemplate.opsForValue();
		Long userId = valueOperations.get(refreshToken);

		if (Objects.isNull(userId)) {
			return Optional.empty();
		}

		return Optional.of(RefreshTokenRedisEntity.create(refreshToken, userId));
	}
}
