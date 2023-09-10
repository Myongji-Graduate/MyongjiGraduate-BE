package com.plzgraduate.myongjigraduatebe.auth.application.service.signin;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.signin.SignInResponse;
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

	@Override
	public SignInResponse signIn(SignInCommand signInCommand) {
		Authentication authentication = authenticateCommand(signInCommand);
		String accessToken = tokenProvider.generateToken(authentication);
		return SignInResponse.from(accessToken);
	}

	private Authentication authenticateCommand(SignInCommand signInCommand) {
		JwtAuthenticationToken authenticationToken =
			new JwtAuthenticationToken(signInCommand.getAuthId(), signInCommand.getPassword());
		Authentication authentication = authenticationManager.authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
}
