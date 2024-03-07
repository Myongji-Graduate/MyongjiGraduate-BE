package com.plzgraduate.myongjigraduatebe.auth.application.service.signin;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.application.usecase.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.usecase.signin.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.api.signin.dto.response.TokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.port.SaveRefreshTokenPort;
import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationToken;
import com.plzgraduate.myongjigraduatebe.auth.security.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class SignInService implements SignInUseCase {

	private final TokenProvider tokenProvider;
	private final AuthenticationManager authenticationManager;
	private final SaveRefreshTokenPort saveRefreshTokenPort;

	@Override
	public TokenResponse signIn(SignInCommand signInCommand) {
		Authentication authentication = authenticateCommand(signInCommand);
		Long userId = (Long) authentication.getPrincipal();
		String accessToken = tokenProvider.generateToken(userId);
		String refreshToken = tokenProvider.generateRefreshToken();
		saveRefreshTokenPort.saveRefreshToken(refreshToken, userId);
		return TokenResponse.from(accessToken, refreshToken);
	}

	private Authentication authenticateCommand(SignInCommand signInCommand) {
		JwtAuthenticationToken authenticationToken =
			new JwtAuthenticationToken(signInCommand.getAuthId(), signInCommand.getPassword());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}
