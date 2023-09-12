package com.plzgraduate.myongjigraduatebe.support;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

import com.plzgraduate.myongjigraduatebe.support.WithMockAuthenticationUserSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAuthenticationUserSecurityContext.class)
public @interface WithMockAuthenticationUser {
	long id() default 1L;

	String authId() default "tester00";
}
