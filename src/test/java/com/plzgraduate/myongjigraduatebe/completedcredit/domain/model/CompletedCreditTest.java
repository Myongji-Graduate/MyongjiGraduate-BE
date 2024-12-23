package com.plzgraduate.myongjigraduatebe.completedcredit.domain.model;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompletedCreditTest {

    @Test
    @DisplayName("편입생의 채플 요건은 0.5 학점이며, 이수 여부를 확인한다.")
    void createChapelCompletedCreditModelForTransferStudent() {
        // given
        User transferStudent = User.builder()
                .id(1L)
                .studentCategory(StudentCategory.TRANSFER) // 편입생
                .build();

        ChapelResult chapelResult = ChapelResult.builder()
                .takenCount(1) // 채플 1회 이수
                .build();

        // when
        CompletedCredit completedCredit = CompletedCredit.createChapelCompletedCreditModel(chapelResult, transferStudent);

        // then
        assertThat(completedCredit.getGraduationCategory()).isEqualTo(GraduationCategory.CHAPEL);
        assertThat(completedCredit.getTotalCredit()).isEqualTo(0.5); // 편입생의 채플 요건
        assertThat(completedCredit.getTakenCredit()).isEqualTo(0.5); // 채플 1회 이수
    }

    @Test
    @DisplayName("편입생이 채플을 이수하지 않았을 경우, 이수 여부를 확인한다.")
    void createChapelCompletedCreditModelForUncompletedTransferStudent() {
        // given
        User transferStudent = User.builder()
                .id(1L)
                .studentCategory(StudentCategory.TRANSFER) // 편입생
                .build();

        ChapelResult chapelResult = ChapelResult.builder()
                .takenCount(0) // 채플 이수 없음
                .build();

        // when
        CompletedCredit completedCredit = CompletedCredit.createChapelCompletedCreditModel(chapelResult, transferStudent);

        // then
        assertThat(completedCredit.getGraduationCategory()).isEqualTo(GraduationCategory.CHAPEL);
        assertThat(completedCredit.getTotalCredit()).isEqualTo(0.5); // 편입생의 채플 요건
        assertThat(completedCredit.getTakenCredit()).isEqualTo(0.0); // 채플 미이수
    }
}
