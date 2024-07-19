package com.plzgraduate.myongjigraduatebe.core.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {

	private final String errorCode;

	private ExceptionResponse(String errorCode) {
		this.errorCode = errorCode;
	}

	public static ExceptionResponse from(String errorCode) {
		return new ExceptionResponse(errorCode);
	}
}
