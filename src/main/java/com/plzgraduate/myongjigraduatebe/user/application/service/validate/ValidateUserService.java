package com.plzgraduate.myongjigraduatebe.user.application.service.validate;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.validate.ValidateUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ValidateUserService implements ValidateUserUseCase {

	private final FindUserUseCase findUserUseCase;
	@Override
	public boolean validateUser(String studentNumber, String authId) {
		User user = findUserUseCase.findUserByStudentNumber(studentNumber);
		return user.isMyAuthId(authId);
	}
}
