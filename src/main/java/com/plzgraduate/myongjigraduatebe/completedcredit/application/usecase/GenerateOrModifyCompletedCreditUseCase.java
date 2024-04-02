package com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface GenerateOrModifyCompletedCreditUseCase {

	void generateOrModifyCompletedCredit(User user, GraduationResult graduationResult);
}
