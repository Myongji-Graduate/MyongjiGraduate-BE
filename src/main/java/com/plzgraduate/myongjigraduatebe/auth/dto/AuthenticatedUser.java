package com.plzgraduate.myongjigraduatebe.auth.dto;

import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUser {
  private final long id;
  private final String userId;
  private final String password;
  private final String studentNumber;
  private final int entryYear;
  private final String name;
  private final EnglishLevel engLv;
  private final String departmentName;
  private final User.Role role;

  public static AuthenticatedUser from(User user) {
    long id = user.getId();
    String userId = user
        .getUserId()
        .getId();
    String password = user.getPassword();
    String studentNumber = user.getStudentNumber() == null
        ? null
        : user
        .getStudentNumber()
        .getValue();
    int entryYear = user
        .getEntryYear() == null
        ? -1
        : user
        .getEntryYear()
        .getValue();
    String name = user.getName();
    EnglishLevel engLv = user.getEngLv();
    String departmentName = user.getDepartment() == null
        ? null
        : user
        .getDepartment()
        .getName();
    User.Role role = user.getRole();
    return new AuthenticatedUser(id, userId, password, studentNumber, entryYear, name, engLv, departmentName, role);
  }
}
