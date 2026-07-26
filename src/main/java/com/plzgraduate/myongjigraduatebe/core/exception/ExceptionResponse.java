package com.plzgraduate.myongjigraduatebe.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExceptionResponse {

	private final String errorCode;
	private final String trackingCode;

	private ExceptionResponse(String errorCode, String trackingCode) {
		this.errorCode = errorCode;
		this.trackingCode = trackingCode;
	}

	public static ExceptionResponse from(String errorCode) {
		return new ExceptionResponse(errorCode, null);
	}

	public static ExceptionResponse tracked(String errorCode, String trackingCode) {
		return new ExceptionResponse(errorCode, trackingCode);
	}
}
