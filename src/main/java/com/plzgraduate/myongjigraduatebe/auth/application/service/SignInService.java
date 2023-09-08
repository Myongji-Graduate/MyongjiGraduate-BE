package com.plzgraduate.myongjigraduatebe.auth.application.service;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.application.port.SignInUseCase;
import com.plzgraduate.myongjigraduatebe.auth.application.port.command.SignInCommand;
import com.plzgraduate.myongjigraduatebe.auth.application.port.response.SignInResponse;
import com.plzgraduate.myongjigraduatebe.auth.jwt.JwtAuthenticationToken;
import com.plzgraduate.myongjigraduatebe.auth.jwt.TokenProvider;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class SignInService implements SignInUseCase {

	private final TokenProvider tokenProvider;

	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	@Override
	public SignInResponse signIn(SignInCommand signInCommand) {
		Authentication authentication = authenticateCommand(signInCommand);
		String accessToken = tokenProvider.generateToken(authentication);
		return SignInResponse.from(accessToken);
	}

	private Authentication authenticateCommand(SignInCommand signInCommand) {
		JwtAuthenticationToken authenticationToken =
			new JwtAuthenticationToken(signInCommand.getAuthId(), signInCommand.getPassword());
		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return authentication;
	}
}
