package com.plzgraduate.myongjigraduatebe.core.exception;

import lombok.Getter;

@Getter
public class AnonymousGraduationCheckException extends RuntimeException {

	private final String trackingCode;

	public AnonymousGraduationCheckException(String trackingCode, Exception cause) {
		super(cause.getMessage(), cause);
		this.trackingCode = trackingCode;
	}
}
