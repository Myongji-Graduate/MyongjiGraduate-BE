package com.plzgraduate.myongjigraduatebe.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = MockUserIsInitializedSecurityContextFactory.class)
public @interface WithMockUserIsInitialized {
}
