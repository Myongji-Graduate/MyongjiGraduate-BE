package com.plzgraduate.myongjigraduatebe.user.application.port.in.validate;

public interface ValidateUserUseCase {

	ValidateUserResponse validateUser(String studentNumber, String authId);
}
