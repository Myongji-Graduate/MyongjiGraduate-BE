package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface GenerateOrModifyCompletedCreditPort {

	void generateOrModifyCompletedCredits(User user, List<CompletedCredit> completedCredits);
}
