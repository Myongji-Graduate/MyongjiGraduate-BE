package com.plzgraduate.myongjigraduatebe.user.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CollegeTest {

    @Test
    void testFindBelongingCollege_Before2025() {
        // given
        String major = "법학과";
        int entryYear = 24; // 2024년까지 법과대

        // when
        College college = College.findBelongingCollege(major, entryYear);

        // then
        assertEquals(College.LAW, college);
    }

    @Test
    void testFindBelongingCollege_After2025() {
        // given
        String major = "법학과";
        int entryYear = 25; // 2025년부터 사회과학대

        // when
        College college = College.findBelongingCollege(major, entryYear);

        // then
        assertEquals(College.SOCIAL_SCIENCE_NEW, college);
    }

    @Test
    void testFindBelongingCollege_OtherMajors() {
        // given
        String major = "경영학전공";
        int entryYear = 22; // 2024년까지는 기존 경영대

        // when
        College college = College.findBelongingCollege(major, entryYear);

        // then
        assertEquals(College.BUSINESS, college);
    }

    @Test
    void testFindBelongingCollege_NewBusinessMajor() {
        // given
        String major = "글로벌비즈니스학전공";
        int entryYear = 25; // 2025년부터 신설된 경영대

        // when
        College college = College.findBelongingCollege(major, entryYear);

        // then
        assertEquals(College.BUSINESS_NEW, college);
    }

    @Test
    void testFindBelongingCollege_ThrowsExceptionForInvalidMajor() {
        // given
        String invalidMajor = "없는전공";
        int entryYear = 24;

        // when & then
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> College.findBelongingCollege(invalidMajor, entryYear));

        assertEquals("소속 단과대가 존재하지 않습니다.", exception.getMessage());
    }
}