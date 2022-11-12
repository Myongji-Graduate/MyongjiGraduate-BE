package com.plzgraduate.myongjigraduatebe.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

import com.plzgraduate.myongjigraduatebe.auth.PrincipalDetails;
import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

public class MockUserIsInitializedSecurityContextFactory implements WithSecurityContextFactory<WithMockUserIsInitialized> {
  @Override
  public SecurityContext createSecurityContext(WithMockUserIsInitialized annotation) {
    final SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

    final Long id = 1L;
    final UserId userId = UserId.valueOf("testUser");
    final String password = "testUserPassword";
    final StudentNumber studentNumber = StudentNumber.valueOf("12345678");
    final EnglishLevel engLv = EnglishLevel.ENG34;

    User user = new User(userId, password, studentNumber, engLv);

    final String userName = "testUserName";
    final Department department = new Department("testDepartment");

    ReflectionTestUtils.setField(user, "id", id);
    ReflectionTestUtils.setField(user, "name", userName);
    ReflectionTestUtils.setField(user, "department", department);

    PrincipalDetails principalDetails = new PrincipalDetails(AuthenticatedUser.from(user));
    Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

    securityContext.setAuthentication(authentication);

    return securityContext;
  }
}
