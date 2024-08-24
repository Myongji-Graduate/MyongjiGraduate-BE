package com.plzgraduate.myongjigraduatebe.auth.application.service.signin;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Collections;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.response.TokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.SaveRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationToken;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;

@ExtendWith(MockitoExtension.class)
class SignInServiceTest {

	@Mock
	private TokenProvider tokenProvider;
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private SaveRefreshTokenPort saveRefreshTokenPort;
	@InjectMocks
	private SignInService signInService;



	@DisplayName("로그인을 진행한다.")
	@Test
	void singIn() {
	    //given
		String authId = "mju-graduate";
		String password = "1q2w3e4r!";
		Long userId = 1L;
		String accessToken = "accessToken";
		String refreshToken = "refreshToken";

		Authentication authentication = new JwtAuthenticationToken(
			userId, null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

		given(authenticationManager.authenticate(any(JwtAuthenticationToken.class)))
			.willReturn(authentication);
		given(tokenProvider.generateToken(userId)).willReturn(accessToken);
		given(tokenProvider.generateRefreshToken()).willReturn(refreshToken);

		//when
		TokenResponse tokenResponse = signInService.signIn(authId, password);

		//then
		then(saveRefreshTokenPort).should(times(1)).saveRefreshToken(refreshToken, userId);
		assertThat(tokenResponse.getAccessToken()).isEqualTo(accessToken);
		assertThat(tokenResponse.getRefreshToken()).isEqualTo(refreshToken);
	}

}
