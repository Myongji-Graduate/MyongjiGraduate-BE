package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransferGraduationRequirementTypeTest {

    @Test
    @DisplayName("단과대 이름으로 편입 졸업 요건을 찾는다.")
    void findByCollegeNameSuccess() {
        // given
        String collegeName = "경영대";

        // when
        TransferGraduationRequirementType result = TransferGraduationRequirementType.findByCollegeName(collegeName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getCollegeName()).isEqualTo(collegeName);
        assertThat(result.getCombinedCultureCredit()).isEqualTo(45);
        assertThat(result.getChristianCredit()).isEqualTo(2);
    }

    @Test
    @DisplayName("존재하지 않는 단과대 이름으로 편입 졸업 요건 조회 시 예외가 발생한다.")
    void findByCollegeNameFailure() {
        // given
        String invalidCollegeName = "의과대";

        // when & then
        assertThatThrownBy(() -> TransferGraduationRequirementType.findByCollegeName(invalidCollegeName))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("해당 단과대의 편입 졸업 요건이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("모든 단과대 편입 졸업 요건을 확인한다.")
    void verifyAllRequirements() {
        // when
        TransferGraduationRequirementType humanities = TransferGraduationRequirementType.HUMANITIES;
        TransferGraduationRequirementType ict = TransferGraduationRequirementType.ICT;

        // then
        assertThat(humanities.getCollegeName()).isEqualTo("인문대");
        assertThat(humanities.getCombinedCultureCredit()).isEqualTo(51);
        assertThat(humanities.getChristianCredit()).isEqualTo(2);

        assertThat(ict.getCollegeName()).isEqualTo("ICT융합대");
        assertThat(ict.getCombinedCultureCredit()).isEqualTo(57);
        assertThat(ict.getChristianCredit()).isEqualTo(2);
    }
}
