package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Password {

  private static final int MIN_LENGTH = 8;
  private static final int MAX_LENGTH = 20;
  private static final String REG = String.format("^(?=.*[!@#$%%^&*])(?=.*[a-zA-Z0-9]).{%d,%d}$", MIN_LENGTH, MAX_LENGTH);

  @JsonDeserialize(using = PasswordDeserializer.class)
  private final String value;

  private static void validate(String value) {
    if (!Pattern.matches(REG, value)) {
      throw new IllegalArgumentException("비밀번호의 형식이 올바르지 않습니다.");
    }
  }

  public static Password of(String value) {
    validate(value);
    return new Password(value);
  }

}
