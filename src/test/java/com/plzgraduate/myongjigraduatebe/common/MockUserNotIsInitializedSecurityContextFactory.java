package com.plzgraduate.myongjigraduatebe.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.plzgraduate.myongjigraduatebe.auth.PrincipalDetails;
import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

public class MockUserNotIsInitializedSecurityContextFactory implements WithSecurityContextFactory<WithMockUserIsNotInitialized> {
  @Override
  public SecurityContext createSecurityContext(WithMockUserIsNotInitialized annotation) {
    final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

    final Long id = 1L;
    final UserId userId = UserId.valueOf("testUser");
    final String password = "testUserPassword";
    final StudentNumber studentNumber = StudentNumber.valueOf("12345678");
    final EnglishLevel engLv = EnglishLevel.MIDDLE;

    User user = new User(userId, password, studentNumber, engLv);

    ReflectionTestUtils.setField(user, "id", id);

    PrincipalDetails principalDetails = new PrincipalDetails(AuthenticatedUser.from(user));
    Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
