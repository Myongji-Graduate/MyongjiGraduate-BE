package com.plzgraduate.myongjigraduatebe.support;

import com.plzgraduate.myongjigraduatebe.auth.security.JwtAuthenticationToken;
import java.util.Collections;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockAuthenticationUserSecurityContext
	implements WithSecurityContextFactory<WithMockAuthenticationUser> {

	@Override
	public SecurityContext createSecurityContext(WithMockAuthenticationUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		JwtAuthenticationToken authentication =
			new JwtAuthenticationToken(
				annotation.id(), null,
				Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"))
			);
		context.setAuthentication(authentication);
		return context;
	}
}
