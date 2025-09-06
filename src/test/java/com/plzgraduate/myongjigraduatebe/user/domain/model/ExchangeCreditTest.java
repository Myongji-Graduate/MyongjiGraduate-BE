package com.plzgraduate.myongjigraduatebe.user.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeCreditTest {

    @Nested
    @DisplayName("from(String) 하위호환 파싱")
    class FromParsing {
        @Test
        @DisplayName("null 입력이면 empty() 반환")
        void from_null_returnsEmpty() {
            ExchangeCredit ec = ExchangeCredit.from(null);
            assertNotNull(ec);
            assertEquals(ExchangeCredit.empty(), ec);
        }

        @Test
        @DisplayName("빈 문자열이면 empty() 반환")
        void from_blank_returnsEmpty() {
            ExchangeCredit ec = ExchangeCredit.from("   ");
            assertNotNull(ec);
            assertEquals(ExchangeCredit.empty(), ec);
        }

        @Test
        @DisplayName("\"0\" 입력이면 empty() 반환")
        void from_zero_returnsEmpty() {
            ExchangeCredit ec = ExchangeCredit.from("0");
            assertNotNull(ec);
            assertEquals(ExchangeCredit.empty(), ec);
        }

        @Test
        @DisplayName("9필드(신규) 정상 파싱")
        void from_9fields_parses() {
            String s = "1/2/3/4/5/6/7/8/9"; // common/basic/normal/major/dualBasic/dualMajor/fusion/sub/free
            ExchangeCredit ec = ExchangeCredit.from(s);

            assertAll(
                () -> assertEquals(1, ec.getCommonCulture()),
                () -> assertEquals(2, ec.getBasicAcademicalCulture()),
                () -> assertEquals(3, ec.getNormalCulture()),
                () -> assertEquals(4, ec.getMajor()),
                () -> assertEquals(5, ec.getDualBasicAcademicalCulture()),
                () -> assertEquals(6, ec.getDualMajor()),
                () -> assertEquals(7, ec.getFusionMajor()),
                () -> assertEquals(8, ec.getSubMajor()),
                () -> assertEquals(9, ec.getFreeElective())
            );
        }

        @Test
        @DisplayName("8필드(레거시) 파싱 시 commonCulture=0 보정")
        void from_8fields_legacy_parsesWithCommonZero() {
            String s = "11/12/13/14/15/16/17/18"; // basic/normal/major/dualBasic/dualMajor/fusion/sub/free
            ExchangeCredit ec = ExchangeCredit.from(s);

            assertAll(
                () -> assertEquals(0, ec.getCommonCulture()),
                () -> assertEquals(11, ec.getBasicAcademicalCulture()),
                () -> assertEquals(12, ec.getNormalCulture()),
                () -> assertEquals(13, ec.getMajor()),
                () -> assertEquals(14, ec.getDualBasicAcademicalCulture()),
                () -> assertEquals(15, ec.getDualMajor()),
                () -> assertEquals(16, ec.getFusionMajor()),
                () -> assertEquals(17, ec.getSubMajor()),
                () -> assertEquals(18, ec.getFreeElective())
            );
        }

        @Test
        @DisplayName("잘못된 필드 개수면 예외")
        void from_invalidCount_throws() {
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("1/2/3"));
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("1/2/3/4/5/6"));
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("1/2/3/4/5/6/7"));
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("1/2/3/4/5/6/7/8/9/10"));
        }

        @Test
        @DisplayName("비숫자 포함 시 예외")
        void from_nonNumeric_throws() {
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("a/b/c/d/e/f/g/h/i"));
            assertThrows(IllegalArgumentException.class, () -> ExchangeCredit.from("1/2/3/4/x/6/7/8/9"));
        }
    }

    @Nested
    @DisplayName("동등성/해시")
    class EqualityHash {
        @Test
        @DisplayName("동일 값이면 equals/hashCode 동일")
        void equals_and_hashCode() {
            ExchangeCredit a = ExchangeCredit.from("1/2/3/4/5/6/7/8/9");
            ExchangeCredit b = ExchangeCredit.from("1/2/3/4/5/6/7/8/9");
            assertEquals(a, b);
            assertEquals(a.hashCode(), b.hashCode());
        }

        @Test
        @DisplayName("서로 다른 값이면 equals 다름")
        void not_equals_when_different() {
            ExchangeCredit a = ExchangeCredit.from("1/2/3/4/5/6/7/8/9");
            ExchangeCredit c = ExchangeCredit.from("0/2/3/4/5/6/7/8/9");
            assertNotEquals(a, c);
        }
    }

    @Test
    @DisplayName("toString() -> from() 라운드트립(9필드)")
    void toString_roundTrip_9fields() {
        ExchangeCredit original = ExchangeCredit.from("1/2/3/4/5/6/7/8/9");
        ExchangeCredit round = ExchangeCredit.from(original.toString());
        assertEquals(original, round);
    }
}