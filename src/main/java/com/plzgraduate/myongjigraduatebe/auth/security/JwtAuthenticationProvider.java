package com.plzgraduate.myongjigraduatebe.auth.security;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.*;
import static org.hibernate.validator.internal.util.TypeHelper.isAssignable;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.exception.UnAuthorizedException;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final PasswordEncoder passwordEncoder;

	private final FindUserUseCase findUserUseCase;

	@Override
	public boolean supports(Class<?> authentication) {
		return isAssignable(JwtAuthenticationToken.class, authentication);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
		return processAuthentication(authenticationToken);
	}

	private Authentication processAuthentication(JwtAuthenticationToken authenticationToken) {
		User user = findUserUseCase.findUserByAuthId(String.valueOf(authenticationToken.getPrincipal()));
		String userPassword = String.valueOf(authenticationToken.getCredentials());
		if(!user.matchPassword(passwordEncoder, userPassword)) {
			throw new UnAuthorizedException(INCORRECT_PASSWORD.toString());
		}
		return new JwtAuthenticationToken(
			user.getId(), null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}
}
