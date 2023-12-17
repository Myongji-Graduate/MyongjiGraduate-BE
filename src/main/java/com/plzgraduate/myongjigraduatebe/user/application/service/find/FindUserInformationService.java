package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserInformationUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserInformationResponse;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
class FindUserInformationService implements FindUserInformationUseCase {

	private final FindUserUseCase findUserUseCase;
	@Override
	public UserInformationResponse findUserInformation(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		return UserInformationResponse.builder()
			.studentName(user.getName())
			.studentNumber(user.getStudentNumber())
			.major(user.getMajor()).build();
	}
}
