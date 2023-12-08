package com.plzgraduate.myongjigraduatebe.user.application.service.withdraw;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.parsing.application.port.out.DeleteParsingTextHistoryPort;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.in.delete.DeleteTakenLectureByUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.adaptor.in.web.withdraw.WithDrawRequest;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.in.withdraw.WithDrawCommand;
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
	private final DeleteParsingTextHistoryPort deleteParsingTextHistoryPort;
	private final DeleteUserPort deleteUserPort;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void withDraw(Long userId, WithDrawCommand withDrawCommand) {
		User user = findUserUseCase.findUserById(userId);
		user.matchPassword(passwordEncoder, withDrawCommand.getPassword());
		deleteTakenLectureByUserUseCase.deleteAllTakenLecturesByUser(user);
		deleteParsingTextHistoryPort.deleteUserParsingTextHistory(user);
		deleteUserPort.deleteUser(user);
	}
}
