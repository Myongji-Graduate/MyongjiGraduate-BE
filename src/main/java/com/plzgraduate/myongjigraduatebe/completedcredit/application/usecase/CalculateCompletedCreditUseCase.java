package com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase;

import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;

public interface CalculateCompletedCreditUseCase {
    List<CompletedCredit> calculateCompletedCredits(User user, TakenLectureInventory inventory);
}
