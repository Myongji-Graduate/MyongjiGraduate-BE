package com.plzgraduate.myongjigraduatebe.user.application.service.find;

import java.util.NoSuchElementException;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserAuthIdUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.UserAuthIdResponse;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindUserAuthIdService implements FindUserAuthIdUseCase {

	private final FindUserPort findUserPort;

	@Override
	public UserAuthIdResponse findUserAuthId(String studentNumber) {
		User user = findUserPort.findUserByStudentNumber(studentNumber)
			.orElseThrow(() -> new NoSuchElementException("해당 학번의 사용자가 존재하지 않습니다"));
		return UserAuthIdResponse.of(user.getEncryptedAuthId(), studentNumber);
	}
}
