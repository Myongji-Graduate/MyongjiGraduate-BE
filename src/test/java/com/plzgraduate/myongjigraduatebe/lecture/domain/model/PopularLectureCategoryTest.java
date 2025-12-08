package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PopularLectureCategoryTest {

    @Test
    @DisplayName("NORMAL_CULTURE 상수가 존재하고 한글명이 일치한다")
    void normalCulture_exists_and_hasKoreanName() {
        PopularLectureCategory c = PopularLectureCategory.NORMAL_CULTURE;
        assertThat(c).isNotNull();
        assertThat(c.name()).isEqualTo("NORMAL_CULTURE");
        assertThat(c.getName()).isEqualTo("일반교양");
        assertThat(c.checkMandatoryIfSeperatedByMandatoryAndElective()).isFalse();
    }

    @Test
    @DisplayName("of(name): 한글명으로 enum을 찾는다")
    void of_looksUpByKoreanName() {
        PopularLectureCategory c = PopularLectureCategory.of("전공필수");
        assertThat(c).isEqualTo(PopularLectureCategory.MANDATORY_MAJOR);
        assertThat(c.checkMandatoryIfSeperatedByMandatoryAndElective()).isTrue();
    }

    @Test
    @DisplayName("of(name): 존재하지 않으면 예외를 던진다")
    void of_throwsOnUnknown() {
        assertThrows(IllegalArgumentException.class, () -> PopularLectureCategory.of("없는카테고리"));
    }
}

