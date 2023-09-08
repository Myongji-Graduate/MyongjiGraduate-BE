package com.plzgraduate.myongjigraduatebe.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockCustomUserSecurityContext implements WithSecurityContextFactory<WithMockCustomUser>{
	@Override
	public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
		SecurityContext context = SecurityContextHolder.createEmptyContext();

		return null;
	}
}
