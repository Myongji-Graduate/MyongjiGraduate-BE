package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserId {

  private static final int MIN_LENGTH = 6;
  private static final int MAX_LENGTH = 20;

  private final String id;

  @JsonCreator
  public static UserId valueOf(String id) {
    validate(id);
    return new UserId(id);
  }

  private static void validate(String id) {
    if (id.length() < MIN_LENGTH || MAX_LENGTH < id.length()) {
      throw new IllegalArgumentException("사용자의 아이디가 올바른 형식이 아닙니다");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    UserId userId = (UserId)o;
    return Objects.equals(id, userId.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
