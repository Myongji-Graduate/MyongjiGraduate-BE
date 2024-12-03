package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindUserAuthIdService implements FindUserAuthIdUseCase {

	private final FindUserUseCase findUserUseCase;

	@Override
	public String findUserAuthId(String studentNumber) {
		User user = findUserUseCase.findUserByStudentNumber(studentNumber);
		return user.getEncryptedAuthId();
	}
}
