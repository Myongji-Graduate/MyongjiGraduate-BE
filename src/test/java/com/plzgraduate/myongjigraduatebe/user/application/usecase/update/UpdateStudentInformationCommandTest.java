package com.plzgraduate.myongjigraduatebe.user.application.usecase.update;

import static org.assertj.core.api.Assertions.assertThat;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationResult;
import com.plzgraduate.myongjigraduatebe.parsing.domain.ParsingInformation;
import com.plzgraduate.myongjigraduatebe.user.domain.model.ExchangeCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.StudentCategory;
import com.plzgraduate.myongjigraduatebe.user.domain.model.TransferCredit;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateStudentInformationCommandTest {

    @Test
    @DisplayName("ParsingInformation을 기반으로 UpdateStudentInformationCommand 생성 테스트")
    void createFromParsingInformation() {
        // given
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .build();

        ParsingInformation parsingInformation = ParsingInformation.builder()
                .studentName("홍길동")
                .major("응용소프트웨어전공")
                .dualMajor("경영학전공")
                .subMajor("통계학과")
                .transferCredit(TransferCredit.from("0/0/0/0"))
                .exchangeCredit(ExchangeCredit.from("0/0/0/0/0/0/0/0"))
                .studentCategory(StudentCategory.NORMAL)
                .build();

        // when
        UpdateStudentInformationCommand command = UpdateStudentInformationCommand.of(user, parsingInformation);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo("홍길동");
        assertThat(command.getMajor()).isEqualTo("응용소프트웨어전공");
        assertThat(command.getDualMajor()).isEqualTo("경영학전공");
        assertThat(command.getSubMajor()).isEqualTo("통계학과");
        assertThat(command.getTransferCredit()).isEqualTo(TransferCredit.from("0/0/0/0"));
        assertThat(command.getExchangeCredit()).isEqualTo(ExchangeCredit.from("0/0/0/0/0/0/0/0"));
        assertThat(command.getStudentCategory()).isEqualTo(StudentCategory.NORMAL);
    }

    @Test
    @DisplayName("GraduationResult를 기반으로 UpdateStudentInformationCommand 업데이트 테스트")
    void updateFromGraduationResult() {
        // given
        User user = User.builder()
                .id(1L)
                .name("홍길동")
                .primaryMajor("응용소프트웨어전공")
                .dualMajor("경영학전공")
                .subMajor("통계학과")
                .studentCategory(StudentCategory.DUAL_MAJOR)
                .build();

        GraduationResult graduationResult = GraduationResult.builder()
                .totalCredit(130)
                .takenCredit(120)
                .graduated(true)
                .build();

        // when
        UpdateStudentInformationCommand command = UpdateStudentInformationCommand.update(user, graduationResult);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo("홍길동");
        assertThat(command.getMajor()).isEqualTo("응용소프트웨어전공");
        assertThat(command.getDualMajor()).isEqualTo("경영학전공");
        assertThat(command.getSubMajor()).isEqualTo("통계학과");
        assertThat(command.getStudentCategory()).isEqualTo(StudentCategory.DUAL_MAJOR);
        assertThat(command.getTotalCredit()).isEqualTo(130);
        assertThat(command.getTakenCredit()).isEqualTo(120);
        assertThat(command.isGraduate()).isTrue();
    }
}
