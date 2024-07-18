package com.plzgraduate.myongjigraduatebe.core.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {

	private final ErrorCode errorCode;

	private ExceptionResponse(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public static ExceptionResponse from(ErrorCode errorCode) {
		return new ExceptionResponse(errorCode);
	}
}
