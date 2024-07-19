package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import static com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.*;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FindUserService implements FindUserUseCase {

	private final static String NOT_FOUND_USER_ERROR_MESSAGE = "해당 사용자를 찾을 수 없습니다.";
	private final FindUserPort findUserPort;

	@Override
	public User findUserById(Long id) {
		return findUserPort.findUserById(id)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_USER_ERROR_MESSAGE));
	}

	@Override
	public User findUserByAuthId(String authId) {
		return findUserPort.findUserByAuthId(authId)
			.orElseThrow(() -> new IllegalArgumentException(NOT_FOUND_AUTHID.toString()));
	}

	@Override
	public User findUserByStudentNumber(String studentNumber) {
		return findUserPort.findUserByStudentNumber(studentNumber)
			.orElseThrow(() -> new IllegalArgumentException(UNREGISTERED_USER.toString()));
	}

}
