package com.plzgraduate.myongjigraduatebe.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockCustomUserSecurityContext.class)
public @interface WithMockCustomUser {
	long id() default 1L;

	String authId() default "tester00";
}
