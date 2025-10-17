package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PopularLectureCategoryTest {

    @Test
    @DisplayName("of(name): 각 enum의 한글명으로 역매핑된다")
    void of_withValidNames_returnsEnum() {
        for (PopularLectureCategory c : PopularLectureCategory.values()) {
            PopularLectureCategory found = PopularLectureCategory.of(c.getName());
            assertThat(found).isSameAs(c);
        }
    }

    @Test
    @DisplayName("of(name): 존재하지 않는 이름이면 IllegalArgumentException")
    void of_withInvalidName_throws() {
        assertThatThrownBy(() -> PopularLectureCategory.of("없는카테고리"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("찾을 수 없습니다");
    }

    @Test
    @DisplayName("checkMandatoryIfSeperatedByMandatoryAndElective: 전공필수만 true")
    void checkMandatoryIfSeparated() {
        assertThat(PopularLectureCategory.MANDATORY_MAJOR.checkMandatoryIfSeperatedByMandatoryAndElective()).isTrue();

        assertThat(PopularLectureCategory.ALL.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
        assertThat(PopularLectureCategory.ELECTIVE_MAJOR.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
        assertThat(PopularLectureCategory.CORE_CULTURE.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
        assertThat(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
        assertThat(PopularLectureCategory.COMMON_CULTURE.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
        assertThat(PopularLectureCategory.NORMAL_CULTURE.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
    }

    @Test
    @DisplayName("name(): enum 상수명을 반환한다")
    void nameReturnsConstantName() {
        // Enum의 name() 메서드는 상수명(MANDATORY_MAJOR, ELECTIVE_MAJOR 등)을 반환
        assertThat(PopularLectureCategory.ALL.name()).isEqualTo("ALL");
        assertThat(PopularLectureCategory.MANDATORY_MAJOR.name()).isEqualTo("MANDATORY_MAJOR");
        assertThat(PopularLectureCategory.ELECTIVE_MAJOR.name()).isEqualTo("ELECTIVE_MAJOR");
        assertThat(PopularLectureCategory.CORE_CULTURE.name()).isEqualTo("CORE_CULTURE");
        assertThat(PopularLectureCategory.BASIC_ACADEMICAL_CULTURE.name()).isEqualTo("BASIC_ACADEMICAL_CULTURE");
        assertThat(PopularLectureCategory.COMMON_CULTURE.name()).isEqualTo("COMMON_CULTURE");
        assertThat(PopularLectureCategory.NORMAL_CULTURE.name()).isEqualTo("NORMAL_CULTURE");
    }
}

