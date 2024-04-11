package com.plzgraduate.myongjigraduatebe.user.application.service.withdraw;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.delete.DeleteTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.DeleteUserPort;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.withdraw.WithDrawUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@Transactional
@RequiredArgsConstructor
class WithDrawUserService implements WithDrawUserUseCase {

	private final FindUserUseCase findUserUseCase;
	private final DeleteTakenLectureUseCase deleteTakenLectureByUserUseCase;
	private final DeleteParsingTextHistoryPort deleteParsingTextHistoryPort;
	private final DeleteUserPort deleteUserPort;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void withDraw(Long userId, String password) {
		User user = findUserUseCase.findUserById(userId);
		user.matchPassword(passwordEncoder, password);
		deleteTakenLectureByUserUseCase.deleteAllTakenLecturesByUser(user);
		deleteParsingTextHistoryPort.deleteUserParsingTextHistory(user);
		deleteUserPort.deleteUser(user);
	}
}
