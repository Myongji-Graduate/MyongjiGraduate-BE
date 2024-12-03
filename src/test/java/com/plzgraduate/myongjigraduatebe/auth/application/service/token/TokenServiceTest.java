package com.plzgraduate.myongjigraduatebe.auth.application.service.token;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.never;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;

import com.plzgraduate.myongjigraduatebe.auth.api.token.dto.response.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	@Mock
	private TokenProvider tokenProvider;

	@Mock
	private FindRefreshTokenPort findRefreshTokenPort;

	@InjectMocks
	private TokenService tokenService;

	@DisplayName("accessToken을 생성한다.")
	@Test
	void generateNewToken() {
		//given
		Long userId = 1L;
		String refreshToken = "refreshToken";
		String accessToken = "accessToken";
		given(findRefreshTokenPort.findByRefreshToken(refreshToken)).willReturn(
			Optional.of(userId));
		given(tokenProvider.generateToken(userId)).willReturn(accessToken);

		//when
		AccessTokenResponse accessTokenResponse = tokenService.generateNewToken(refreshToken);

		//then
		assertThat(accessTokenResponse.getAccessToken()).isEqualTo(accessToken);
	}

	@DisplayName("유효하지 않은 토큰일 경우 에러를 반환한다.")
	@Test
	void illegalArgumentExceptionIfInvalidToken() {
		//given
		String refreshToken = "refreshToken";
		given(findRefreshTokenPort.findByRefreshToken(refreshToken)).willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> tokenService.generateNewToken(refreshToken))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효하지 않은 토큰입니다.");

		//then
		then(findRefreshTokenPort).should(times(1))
			.findByRefreshToken(refreshToken);
		then(tokenProvider).should(never())
			.generateToken(anyLong());
	}
}
