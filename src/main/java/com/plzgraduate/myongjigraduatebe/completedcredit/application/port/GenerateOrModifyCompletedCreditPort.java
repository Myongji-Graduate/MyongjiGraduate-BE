package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;

public interface GenerateOrModifyCompletedCreditPort {

	void generateOrModifyCompletedCredits(User user, List<CompletedCredit> completedCredits);
}
