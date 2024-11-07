package com.plzgraduate.myongjigraduatebe.auth.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

	@Mock
	private JwtProperties jwtProperties;
	@InjectMocks
	private TokenProvider tokenProvider;

	@DisplayName("refreshToken을 생성한다.")
	@Test
	void generateRefreshToken() {
		String refreshToken = tokenProvider.generateRefreshToken();
		assertEquals(36, refreshToken.length());
	}
}
