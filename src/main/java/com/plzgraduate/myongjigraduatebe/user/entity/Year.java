package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Objects;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Year {

  private static final int YEAR_LENGTH = 4;
  private static final String YEAR_REG = "^[0-9]{" + YEAR_LENGTH + "}";
  private String value;

  private Year(String value) {
    this.value = value;
  }

  public static Year of(String value) {
    validateYear(value);
    return new Year(value);
  }

  private static void validateYear(String value) {
    if (!Pattern.matches(YEAR_REG, value.substring(0, 4))) {
      throw new IllegalArgumentException("수강년도가 올바르지 않습니다.");
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
    Year year = (Year)o;
    return Objects.equals(value, year.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
