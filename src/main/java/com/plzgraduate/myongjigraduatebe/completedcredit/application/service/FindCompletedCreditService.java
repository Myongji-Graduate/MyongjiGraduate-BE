package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FindCompletedCreditService implements FindCompletedCreditUseCase {

	private final FindUserUseCase findUserUseCase;
	private final FindCompletedCreditPort findCompletedCreditPort;

	@Override
	public List<CompletedCredit> findCompletedCredits(Long userId) {
		User user = findUserUseCase.findUserById(userId);
		return findCompletedCreditPort.findCompletedCredit(user);
	}
}
