package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MajorLectureOfferingTest {

    @Test
    @DisplayName("생성자: 정상적인 값으로 생성 성공")
    void constructor_success() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 3, OfferedSemester.FIRST);

        assertThat(offering.getLectureId()).isEqualTo("LEC001");
        assertThat(offering.getGrade()).isEqualTo(3);
        assertThat(offering.getSemester()).isEqualTo(OfferedSemester.FIRST);
    }

    @Test
    @DisplayName("생성자: lectureId가 null이면 예외 발생")
    void constructor_nullLectureId() {
        assertThatThrownBy(() -> new MajorLectureOffering(null, 3, OfferedSemester.FIRST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lectureId must not be null/blank");
    }

    @Test
    @DisplayName("생성자: lectureId가 빈 문자열이면 예외 발생")
    void constructor_blankLectureId() {
        assertThatThrownBy(() -> new MajorLectureOffering("  ", 3, OfferedSemester.FIRST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("lectureId must not be null/blank");
    }

    @Test
    @DisplayName("생성자: grade가 음수이면 예외 발생")
    void constructor_negativeGrade() {
        assertThatThrownBy(() -> new MajorLectureOffering("LEC001", -1, OfferedSemester.FIRST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("grade must be 0~4");
    }

    @Test
    @DisplayName("생성자: grade가 4 초과이면 예외 발생")
    void constructor_gradeOver4() {
        assertThatThrownBy(() -> new MajorLectureOffering("LEC001", 5, OfferedSemester.FIRST))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("grade must be 0~4");
    }

    @Test
    @DisplayName("of: 정적 팩토리 메서드로 생성 성공")
    void of_success() {
        MajorLectureOffering offering = MajorLectureOffering.of("LEC001", 2, OfferedSemester.SECOND);

        assertThat(offering.getLectureId()).isEqualTo("LEC001");
        assertThat(offering.getGrade()).isEqualTo(2);
        assertThat(offering.getSemester()).isEqualTo(OfferedSemester.SECOND);
    }

    @Test
    @DisplayName("ofCode: 코드로 생성 성공")
    void ofCode_success() {
        MajorLectureOffering offering = MajorLectureOffering.ofCode("LEC001", 3, 1);

        assertThat(offering.getLectureId()).isEqualTo("LEC001");
        assertThat(offering.getGrade()).isEqualTo(3);
        assertThat(offering.getSemester()).isEqualTo(OfferedSemester.FIRST);
    }

    @Test
    @DisplayName("isOfferedInSemester: BOTH는 모든 학기에서 true")
    void isOfferedInSemester_both() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 3, OfferedSemester.BOTH);

        assertThat(offering.isOfferedInSemester(1)).isTrue();
        assertThat(offering.isOfferedInSemester(2)).isTrue();
    }

    @Test
    @DisplayName("isOfferedInSemester: FIRST는 1학기에만 true")
    void isOfferedInSemester_first() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 3, OfferedSemester.FIRST);

        assertThat(offering.isOfferedInSemester(1)).isTrue();
        assertThat(offering.isOfferedInSemester(2)).isFalse();
    }

    @Test
    @DisplayName("isOfferedInSemester: SECOND는 2학기에만 true")
    void isOfferedInSemester_second() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 3, OfferedSemester.SECOND);

        assertThat(offering.isOfferedInSemester(1)).isFalse();
        assertThat(offering.isOfferedInSemester(2)).isTrue();
    }

    @Test
    @DisplayName("matchesGrade: grade가 0이면 모든 학년에 true")
    void matchesGrade_zero() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 0, OfferedSemester.BOTH);

        assertThat(offering.matchesGrade(1)).isTrue();
        assertThat(offering.matchesGrade(2)).isTrue();
        assertThat(offering.matchesGrade(3)).isTrue();
        assertThat(offering.matchesGrade(4)).isTrue();
    }

    @Test
    @DisplayName("matchesGrade: 제공 학년이 현재 학년보다 낮거나 같으면 true")
    void matchesGrade_lowerOrEqual() {
        MajorLectureOffering offering = new MajorLectureOffering("LEC001", 3, OfferedSemester.BOTH);

        // 제공 학년이 3이므로, 현재 학년이 3 이상이면 수강 가능
        assertThat(offering.matchesGrade(3)).isTrue();
        assertThat(offering.matchesGrade(4)).isTrue();
        // 현재 학년이 제공 학년보다 낮으면 수강 불가
        assertThat(offering.matchesGrade(1)).isFalse();
        assertThat(offering.matchesGrade(2)).isFalse();
    }

    @Test
    @DisplayName("getOfferedSemesterCode: 학기 코드 반환")
    void getOfferedSemesterCode() {
        MajorLectureOffering offering1 = new MajorLectureOffering("LEC001", 3, OfferedSemester.FIRST);
        MajorLectureOffering offering2 = new MajorLectureOffering("LEC002", 3, OfferedSemester.SECOND);
        MajorLectureOffering offering3 = new MajorLectureOffering("LEC003", 3, OfferedSemester.BOTH);

        assertThat(offering1.getOfferedSemesterCode()).isEqualTo(1);
        assertThat(offering2.getOfferedSemesterCode()).isEqualTo(2);
        assertThat(offering3.getOfferedSemesterCode()).isEqualTo(3); // BOTH는 3
    }
}

