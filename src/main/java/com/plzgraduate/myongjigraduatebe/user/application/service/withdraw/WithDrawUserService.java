package com.plzgraduate.myongjigraduatebe.user.application.service.withdraw;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.out.DeleteUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class WithDrawUserService implements WithDrawUserUseCase {

	private final FindUserUseCase findUserUseCase;
	private final DeleteTakenLectureByUserUseCase deleteTakenLectureByUserUseCase;
	private final DeleteUserPort deleteUserPort;

	@Override
	public void withDraw(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		deleteTakenLectureByUserUseCase.deleteAllTakenLecturesByUser(user);
		deleteUserPort.deleteUser(user);
	}
}
