package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.CalculateCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional(readOnly = true, isolation = Isolation.REPEATABLE_READ)
class FindCompletedCreditService implements FindCompletedCreditUseCase {
    private final FindTakenLectureUseCase findTakenLectureUseCase;
    private final CalculateCompletedCreditUseCase calculateCompletedCreditUseCase;

    @Override
    public List<CompletedCredit> findCompletedCredits(Long userId) {
        var read = findTakenLectureUseCase.findTakenLecturesWithUser(userId);
        return calculateCompletedCreditUseCase.calculateCompletedCredits(read.user(), read.inventory());
    }
}
