package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.FindCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@UseCase
@RequiredArgsConstructor
@Transactional
class FindCompletedCreditService implements FindCompletedCreditUseCase {

    private final FindUserPort findUserPort;
    private final FindCompletedCreditPort findCompletedCreditPort;
    private final GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;

    @Override
    public List<CompletedCredit> findCompletedCredits(Long userId) {
        // Serialize before any snapshot read: waiting until replacement is too late
        // under REPEATABLE READ. The parent exists even before credits are created.
        User user = findUserPort.findUserByIdForUpdate(userId)
            .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));
        generateOrModifyCompletedCreditUseCase.generateOrModifyCompletedCredit(user);
        return findCompletedCreditPort.findCompletedCredit(user);
    }
}
