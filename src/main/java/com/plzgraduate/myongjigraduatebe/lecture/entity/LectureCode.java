package com.plzgraduate.myongjigraduatebe.lecture.entity;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureCode {

  private static final String ERROR_MESSAGE = "Lecture Code가 올바르지 않습니다.";
  private static final int MIN_CODE_LENGTH = 5;
  private static final int MAX_CODE_LENGTH = 8;
  private static final String CODE_REGEX = "^[A-Z]{2,3}[0-9]{3,5}$";

  @JsonValue
  private String code;

  public static LectureCode of(String code) {
    return new LectureCode(code);
  }

  private LectureCode(String code) {
    validate(code);
    this.code = code;
  }

  private void validate(String code) {
    validateLength(code);
    validateFormat(code);
  }

  private void validateFormat(String code) {
    if (!code.matches(CODE_REGEX)) {
      throw new IllegalArgumentException(ERROR_MESSAGE);
    }
  }

  private void validateLength(String code) {
    if (code.length() < MIN_CODE_LENGTH || MAX_CODE_LENGTH < code.length()) {
      throw new IllegalArgumentException(ERROR_MESSAGE);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (o == this) {
      return true;
    }

    if (o.getClass() != getClass()) {
      return false;
    }

    LectureCode lc = (LectureCode)o;
    return this
        .getCode()
        .equals(lc.getCode());
  }

  @Override
  public int hashCode() {
    int hash = 7;

    hash = 31 * hash + code.hashCode();

    return hash;
  }
}
