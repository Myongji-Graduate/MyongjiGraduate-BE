package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.GenerateOrModifyCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindCompletedCreditServiceTest {

    @Mock
    private FindUserPort findUserPort;
    @Mock
    private FindCompletedCreditPort findCompletedCreditPort;
    @Mock
    private GenerateOrModifyCompletedCreditUseCase generateOrModifyCompletedCreditUseCase;
    @InjectMocks
    private FindCompletedCreditService findCompletedCreditService;

    @DisplayName("유저의 기이수 학점을 조회한다.")
    @Test
    void findCompletedCredits() {
        //given
        User user = User.builder()
            .id(1L)
            .build();
        given(findUserPort.findUserByIdForUpdate(1L)).willReturn(Optional.of(user));
        given(findCompletedCreditPort.findCompletedCredit(user)).willReturn(List.of(
            CompletedCredit.builder()
                .build(),
            CompletedCredit.builder()
                .build(),
            CompletedCredit.builder()
                .build()
        ));

        //when
        List<CompletedCredit> result = findCompletedCreditService.findCompletedCredits(1L);

        //then
        assertThat(result).hasSize(3);
        then(generateOrModifyCompletedCreditUseCase).should()
            .generateOrModifyCompletedCredit(user);
        then(findCompletedCreditPort).should()
            .findCompletedCredit(user);
    }
}
