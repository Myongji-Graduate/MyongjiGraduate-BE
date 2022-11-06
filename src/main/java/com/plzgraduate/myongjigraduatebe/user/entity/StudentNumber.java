package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Objects;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentNumber {

  private static final int LENGTH = 8;
  private static final String REG = "^[0-9]{" + LENGTH + "}";

  private String value;

  private StudentNumber(String value) {
    this.value = value;
  }

  @JsonCreator
  public static StudentNumber valueOf(String value) {
    validate(value);
    return new StudentNumber(value);
  }

  private static void validate(String value) {
    if (value == null || !Pattern.matches(REG, value)) {
      throw new IllegalArgumentException("학번이 올바르지 않습니다.");
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

    StudentNumber that = (StudentNumber)o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
