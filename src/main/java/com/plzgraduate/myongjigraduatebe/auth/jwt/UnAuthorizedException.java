package com.plzgraduate.myongjigraduatebe.auth.jwt;

public class UnAuthorizedException extends RuntimeException{
	public UnAuthorizedException(String message) {super(message);
	}
}
