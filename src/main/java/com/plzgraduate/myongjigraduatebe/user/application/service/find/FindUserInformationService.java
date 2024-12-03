package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindUserInformationService implements FindUserInformationUseCase {

	private final FindUserUseCase findUserUseCase;

	@Override
	public User findUserInformation(Long userId) {
		return findUserUseCase.findUserById(userId);
	}
}
