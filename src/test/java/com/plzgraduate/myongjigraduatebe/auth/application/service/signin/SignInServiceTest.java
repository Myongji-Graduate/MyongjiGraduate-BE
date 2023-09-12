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

import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInResponse;
import com.plzgraduate.myongjigraduatebe.auth.security.AuthenticationUser;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationToken;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;

@ExtendWith(MockitoExtension.class)
class SignInServiceTest {

	@Mock
	private TokenProvider tokenProvider;
	@Mock
	private AuthenticationManager authenticationManager;
	@InjectMocks
	private SignInService signInService;

	@DisplayName("로그인을 진행한다.")
	@Test
	void singIn() {
	    //given
		SignInCommand command = SignInCommand.builder()
			.authId("mju-graduate")
			.password("1q2w3e4r!")
			.build();
		String accessToken = "jwt";

		Authentication authentication = new JwtAuthenticationToken(
			new AuthenticationUser(1L, "mju-graduate"),
			null,
			Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));

		given(authenticationManager.authenticate(any(JwtAuthenticationToken.class)))
			.willReturn(authentication);
		given(tokenProvider.generateToken(any(Authentication.class))).willReturn(accessToken);

		//when
		SignInResponse signInResponse = signInService.signIn(command);

		//then
		assertThat(signInResponse.getAccessToken()).isEqualTo(accessToken);
	}

}