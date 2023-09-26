package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserAuthIdResponse;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindUserAuthIdService implements FindUserAuthIdUseCase {

	private final FindUserUseCase findUserUseCase;

	@Override
	public UserAuthIdResponse findUserAuthId(String studentNumber) {
		User user = findUserUseCase.findUserByStudentNumber(studentNumber);
		return UserAuthIdResponse.of(user.getEncryptedAuthId(), studentNumber);
	}
}
