package com.plzgraduate.myongjigraduatebe.user.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

  private static final int MIN_LENGTH = 6;
  private static final int MAX_LENGTH = 20;

  private final String id;

  public static UserId of(String id) {
    validate(id);
    return new UserId(id);
  }

  private static void validate(String id) {
    if (id.length() < MIN_LENGTH || MAX_LENGTH < id.length()) {
      throw new IllegalArgumentException("사용자의 아이디가 올바른 형식이 아닙니다");
    }
  }
}
