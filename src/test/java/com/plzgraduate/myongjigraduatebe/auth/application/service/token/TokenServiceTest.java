package com.plzgraduate.myongjigraduatebe.auth.application.service.token;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.AccessTokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.token.TokenCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.out.FindRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;

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
	void createNewToken() {
		//given
		Long userId = 1L;
		String refreshToken = "refreshToken";
		String accessToken = "accessToken";
		TokenCommand command = TokenCommand.builder()
			.refreshToken(refreshToken)
				.build();
		given(findRefreshTokenPort.findByRefreshToken(refreshToken)).willReturn(Optional.of(userId));
		given(tokenProvider.generateToken(userId)).willReturn(accessToken);

		//when
		AccessTokenResponse accessTokenResponse = tokenService.createNewToken(command);

		//then
		assertThat(accessTokenResponse.getAccessToken()).isEqualTo(accessToken);
	}

	@DisplayName("유효하지 않은 토큰일 경우 에러를 반환한다.")
	@Test
	void illegalArgumentExceptionIfInvalidToken() {
		//given
		String refreshToken = "refreshToken";
		TokenCommand command = TokenCommand.builder()
			.refreshToken(refreshToken)
			.build();
		given(findRefreshTokenPort.findByRefreshToken(refreshToken)).willReturn(Optional.empty());

		//when
		assertThatThrownBy(() -> tokenService.createNewToken(command))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("유효하지 않은 토큰입니다.");

		//then
		then(findRefreshTokenPort).should(times(1)).findByRefreshToken(refreshToken);
		then(tokenProvider).should(never()).generateToken(anyLong());
	}
}
