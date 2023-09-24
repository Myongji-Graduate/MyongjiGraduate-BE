package com.plzgraduate.myongjigraduatebe.user.application.port.in.check;

public interface CheckAuthIdDuplicationUseCase {

	AuthIdDuplicationResponse checkAuthIdDuplication(String authId);
}
