package com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import java.util.List;

public interface FindCompletedCreditUseCase {

	List<CompletedCredit> findCompletedCredits(Long userId);
}
