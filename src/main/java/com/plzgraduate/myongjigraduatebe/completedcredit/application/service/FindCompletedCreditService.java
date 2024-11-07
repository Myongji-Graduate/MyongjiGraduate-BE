package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
class FindCompletedCreditService implements FindCompletedCreditUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindCompletedCreditPort findCompletedCreditPort;

	@Override
	public List<CompletedCredit> findCompletedCredits(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		return findCompletedCreditPort.findCompletedCredit(user);
	}
}
