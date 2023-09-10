package com.plzgraduate.myongjigraduatebe.security;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import com.plzgraduate.myongjigraduatebe.auth.jwt.AuthenticationUser;
import com.plzgraduate.myongjigraduatebe.auth.jwt.JwtAuthenticationToken;

public class WithMockAuthenticationUserSecurityContext
	implements WithSecurityContextFactory<WithMockAuthenticationUser> {
	@Override
	public SecurityContext createSecurityContext(WithMockAuthenticationUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		JwtAuthenticationToken authentication =
			new JwtAuthenticationToken(
				new AuthenticationUser(annotation.id(), annotation.authId()), null,
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
			);
		context.setAuthentication(authentication);
		return context;
	}
}
