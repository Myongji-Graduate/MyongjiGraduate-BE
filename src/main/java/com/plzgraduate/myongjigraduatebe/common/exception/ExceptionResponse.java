package com.plzgraduate.myongjigraduatebe.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ExceptionResponse {

  private final int code;
  private final String message;

  private ExceptionResponse(
      int code,
      String message
  ) {
    this.code = code;
    this.message = message;
  }

  public static ExceptionResponse of(
      HttpStatus httpStatus,
      String message
  ) {
    return new ExceptionResponse(httpStatus.value(), message);
  }
}
