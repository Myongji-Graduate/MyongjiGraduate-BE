package com.plzgraduate.myongjigraduatebe.user.application.service.check;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.api.signup.dto.response.AuthIdDuplicationResponse;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.check.CheckAuthIdDuplicationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.CheckUserPort;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class CheckAuthIdDuplicationService implements CheckAuthIdDuplicationUseCase {

	private final CheckUserPort checkUserPort;

	@Override
	public AuthIdDuplicationResponse checkAuthIdDuplication(String authId) {
		boolean authIdDuplication = !checkUserPort.checkDuplicateAuthId(authId);
		return AuthIdDuplicationResponse.builder()
			.authId(authId)
			.notDuplicated(authIdDuplication).build();
	}
}
