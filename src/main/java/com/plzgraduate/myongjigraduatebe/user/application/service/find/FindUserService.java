package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FindUserService implements FindUserUseCase {

	private final FindUserPort findUserPort;

	@Override
	public User findUserById(Long id) {
		return findUserPort.findUserById(id)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
	}

	@Override
	public User findUserByAuthId(String authId) {
		return findUserPort.findUserByAuthId(authId)
			.orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
	}
}
