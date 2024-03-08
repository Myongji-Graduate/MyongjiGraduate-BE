package com.plzgraduate.myongjigraduatebe.user.application.usecase.validate;

import com.plzgraduate.myongjigraduatebe.user.api.resetpassword.dto.response.ValidateUserResponse;

public interface ValidateUserUseCase {

	ValidateUserResponse validateUser(String studentNumber, String authId);
}
