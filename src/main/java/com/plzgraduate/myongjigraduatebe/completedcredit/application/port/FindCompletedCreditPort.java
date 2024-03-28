package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindCompletedCreditPort {

	List<CompletedCredit> findCompletedCredit(User user);
}
