package com.plzgraduate.myongjigraduatebe.user.application.usecase.resetpassword;

public interface ResetPasswordUseCase {

	void resetPassword(String authId, String newPassword, String passwordCheck);
}
