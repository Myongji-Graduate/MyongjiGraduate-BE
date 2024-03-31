package com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;

public interface FindCompletedCreditUseCase {

	List<CompletedCredit> findCompletedCredits(Long userId);

}
