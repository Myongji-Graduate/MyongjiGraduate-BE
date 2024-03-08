package com.plzgraduate.myongjigraduatebe.user.application.usecase.check;

import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.AuthIdDuplicationResponse;

public interface CheckAuthIdDuplicationUseCase {

	AuthIdDuplicationResponse checkAuthIdDuplication(String authId);
}
