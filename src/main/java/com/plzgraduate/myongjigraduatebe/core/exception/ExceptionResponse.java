package com.plzgraduate.myongjigraduatebe.core.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class ExceptionResponse {

	private final int status;
	private final String message;

	private ExceptionResponse(
		int status,
		String message
	) {
		this.status = status;
		this.message = message;
	}

	public static ExceptionResponse of(
		HttpStatus httpStatus,
		String message
	) {
		return new ExceptionResponse(httpStatus.value(), message);
	}
}
