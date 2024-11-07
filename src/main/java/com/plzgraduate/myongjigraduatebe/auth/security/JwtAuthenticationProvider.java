package com.plzgraduate.myongjigraduatebe.auth.security;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.INCORRECT_PASSWORD;
import static org.hibernate.validator.internal.util.TypeHelper.isAssignable;

import com.plzgraduate.myongjigraduatebe.core.exception.UnAuthorizedException;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private final PasswordEncoder passwordEncoder;

	private final FindUserUseCase findUserUseCase;

	@Override
	public boolean supports(Class<?> authentication) {
		return isAssignable(JwtAuthenticationToken.class, authentication);
	}

	@Override
	public Authentication authenticate(Authentication authentication)
		throws AuthenticationException {
		JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
		return processAuthentication(authenticationToken);
	}

	private Authentication processAuthentication(JwtAuthenticationToken authenticationToken) {
		User user = findUserUseCase.findUserByAuthId(
			String.valueOf(authenticationToken.getPrincipal()));
		try {
			user.matchPassword(passwordEncoder,
				String.valueOf(authenticationToken.getCredentials()));
			return new JwtAuthenticationToken(
				user.getId(), null, Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
			);
		} catch (IllegalArgumentException e) {
			throw new UnAuthorizedException(INCORRECT_PASSWORD.toString());
		}
	}

}
