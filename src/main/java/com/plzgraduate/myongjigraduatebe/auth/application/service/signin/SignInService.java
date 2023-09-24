package com.plzgraduate.myongjigraduatebe.auth.application.service.signin;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.signin.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.in.TokenResponse;
import com.plzgraduate.myongjigraduatebe.auth.application.service.token.RefreshTokenService;
import com.plzgraduate.myongjigraduatebe.auth.domain.RefreshToken;;
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
	private final RefreshTokenService refreshTokenService;

	@Override
	public TokenResponse signIn(SignInCommand signInCommand) {
		Authentication authentication = authenticateCommand(signInCommand);
		Long userId = (Long) authentication.getPrincipal();
		String accessToken = tokenProvider.generateToken(userId);
		RefreshToken refreshToken = RefreshToken.createToken(userId);
		refreshTokenService.saveRefreshToken(refreshToken);
		return TokenResponse.from(accessToken, refreshToken.getRefreshToken());
	}

	private Authentication authenticateCommand(SignInCommand signInCommand) {
		JwtAuthenticationToken authenticationToken =
			new JwtAuthenticationToken(signInCommand.getAuthId(), signInCommand.getPassword());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}

}
