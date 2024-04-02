package com.plzgraduate.myongjigraduatebe.completedcredit.application.port;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;

public interface GenerateOrModifyCompletedCreditPort {

	void generateOrModifyCompletedCredits(List<CompletedCredit> completedCredits);
}
