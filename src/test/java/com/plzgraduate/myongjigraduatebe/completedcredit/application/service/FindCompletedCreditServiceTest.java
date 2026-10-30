package com.plzgraduate.myongjigraduatebe.completedcredit.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.usecase.CalculateCompletedCreditUseCase;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.usecase.find.FindTakenLectureUseCase;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureInventory;
import com.plzgraduate.myongjigraduatebe.takenlecture.domain.model.TakenLectureRead;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FindCompletedCreditServiceTest {

    @Mock
    private FindTakenLectureUseCase findTakenLectureUseCase;
    @Mock
    private CalculateCompletedCreditUseCase calculateCompletedCreditUseCase;
    @InjectMocks
    private FindCompletedCreditService findCompletedCreditService;

    @DisplayName("유저의 기이수 학점을 조회한다.")
    @Test
    void findCompletedCredits() {
        //given
        User user = User.builder()
            .id(1L)
            .build();
        var inventory = TakenLectureInventory.from(java.util.Set.of());
        given(findTakenLectureUseCase.findTakenLecturesWithUser(1L))
            .willReturn(new TakenLectureRead(user, inventory));
        given(calculateCompletedCreditUseCase.calculateCompletedCredits(user, inventory)).willReturn(List.of(
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
        then(calculateCompletedCreditUseCase).should()
            .calculateCompletedCredits(user, inventory);
        then(findTakenLectureUseCase).should().findTakenLecturesWithUser(1L);
        then(findTakenLectureUseCase).shouldHaveNoMoreInteractions();
    }
}
