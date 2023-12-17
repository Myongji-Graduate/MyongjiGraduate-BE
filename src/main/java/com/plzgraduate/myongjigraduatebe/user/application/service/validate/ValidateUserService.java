package com.plzgraduate.myongjigraduatebe.user.application.service.validate;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.validate.ValidateUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class ValidateUserService implements ValidateUserUseCase {

	private final FindUserUseCase findUserUseCase;
	@Override
	public ValidateUserResponse validateUser(String studentNumber, String authId) {
		User user = findUserUseCase.findUserByStudentNumber(studentNumber);
		boolean validateAuthIdResult = user.isMyAuthId(authId);
		return ValidateUserResponse.builder()
			.passedUserValidation(validateAuthIdResult).build();
	}
}
