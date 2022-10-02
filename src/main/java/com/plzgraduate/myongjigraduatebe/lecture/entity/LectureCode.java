package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class LectureCode {

  private static final String ERROR_MESSAGE = "Lecture Code가 올바르지 않습니다.";
  private static final int CODE_LENGTH = 8;
  private static final String CODE_REGEX = "^[A-Z]{3}[0-9]{5}$";

  @Column(columnDefinition = "CHAR", length = CODE_LENGTH, unique = true)
  private String code;

  protected LectureCode(String code) {
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

  private static void validateLength(String code) {
    if (code.length() != CODE_LENGTH) {
      throw new IllegalArgumentException(ERROR_MESSAGE);
    }
  }
}
