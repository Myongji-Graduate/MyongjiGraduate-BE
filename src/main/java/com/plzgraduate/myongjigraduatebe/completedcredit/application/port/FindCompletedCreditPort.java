package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;

public interface FindCompletedCreditPort {

	List<CompletedCredit> findCompletedCredit(User user);
}
