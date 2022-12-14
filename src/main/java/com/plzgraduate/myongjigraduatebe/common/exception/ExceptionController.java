package com.plzgraduate.myongjigraduatebe.common.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse handleIllegalArgumentException(IllegalArgumentException e) {
    logger.warn("IllegalArgumentException: ", e);
    String message = getMessage(e);
    return ExceptionResponse.of(HttpStatus.BAD_REQUEST, message);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ExceptionResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    logger.warn("HttpMessageNotReadableException: ", e);
    String message = getMessage(e);
    return ExceptionResponse.of(
        HttpStatus.BAD_REQUEST,
        message
    );
  }

  private String getMessage(Exception e) {
    String message = e.getMessage();
    Throwable throwable = e.getCause();
    while (throwable != null) {
      message = throwable.getMessage();
      throwable = throwable.getCause();
    }
    return message;
  }

  @ExceptionHandler(RuntimeException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleRuntimeException(RuntimeException e) {
    logger.error("RuntimeException : ", e);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public void handleException(Exception e) {
    logger.error("Exception : ", e);
  }
}
