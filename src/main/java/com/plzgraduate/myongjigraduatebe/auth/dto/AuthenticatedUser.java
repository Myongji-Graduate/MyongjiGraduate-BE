package com.plzgraduate.myongjigraduatebe.auth.dto;

import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticatedUser {
  private final long id;
  private final UserId userId;
  private final String password;
  private final StudentNumber studentNumber;
  private final EntryYear entryYear;
  private final String name;
  private final EnglishLevel engLv;
  private final Department department;
  private final User.Role role;

  public static AuthenticatedUser from(User user) {
    return new AuthenticatedUser(
        user.getId(),
        user.getUserId(),
        user.getPassword(),
        user.getStudentNumber(),
        user.getEntryYear(),
        user.getName(),
        user.getEngLv(),
        user.getDepartment(),
        user.getRole()
    );
  }
}
