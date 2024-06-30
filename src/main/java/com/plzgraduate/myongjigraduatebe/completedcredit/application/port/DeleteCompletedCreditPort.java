package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface DeleteCompletedCreditPort {

	void deleteAllCompletedCredits(User user);
}
