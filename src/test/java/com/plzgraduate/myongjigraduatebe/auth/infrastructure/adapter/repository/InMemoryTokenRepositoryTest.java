package com.plzgraduate.myongjigraduatebe.auth.infrastructure.adapter.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.auth.infrastructure.adapter.repository.InMemoryTokenRepository;

@ExtendWith(MockitoExtension.class)
class InMemoryTokenRepositoryTest {
	@InjectMocks
	private InMemoryTokenRepository inMemoryTokenRepository;

	@DisplayName("refreshToken을 저장한다.")
	@Test
	void saveRefreshToken() {
		//given
		String refreshToken = "refreshToken";
		Long userId = 1L;

		//when
		inMemoryTokenRepository.saveRefreshToken(refreshToken, userId);

		//then
		Optional<Long> userIdFromCache = inMemoryTokenRepository.findByRefreshToken(refreshToken);
		assertThat(userIdFromCache).isPresent();
		assertThat(userId).isEqualTo(userIdFromCache.get());

	}

	@DisplayName("refreshToken으로 value를 찾는다.")
	@Test
	void findByRefreshToken() {
		//given
		String refreshToken = "refreshToken";
		Long userId1 = 1L;
		Long userId2 = 2L;
		inMemoryTokenRepository.saveRefreshToken(refreshToken, userId1);
		inMemoryTokenRepository.saveRefreshToken(refreshToken, userId2);

		//then
		Optional<Long> userIdFromCache = inMemoryTokenRepository.findByRefreshToken(refreshToken);

		//when
		assertThat(userIdFromCache).isPresent();
		assertThat(userId2).isEqualTo(userIdFromCache.get());
	}

}
