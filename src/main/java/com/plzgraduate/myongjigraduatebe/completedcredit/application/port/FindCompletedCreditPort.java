package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface FindCompletedCreditPort {

	List<CompletedCredit> findCompletedCredits(User user);

	CompletedCredit findCategorizedCompletedCredit(User user, GraduationCategory graduationCategory);
}
