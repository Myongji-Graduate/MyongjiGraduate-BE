package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCredit implements Serializable {
    private static final long serialVersionUID = 1L;

    private int commonCulture;            //공통교양
    private int basicAcademicalCulture;  // 학문기초교양
    private int normalCulture;          // 일반교양
    private int major;                  // 전공
    private int dualBasicAcademicalCulture; // 복수전공학문기초교양
    private int dualMajor;              // 복수전공
    private int fusionMajor;            // 융합전공
    private int subMajor;               // 부전공
    private int freeElective;           // 자유선택

    public static ExchangeCredit empty() {
        return new ExchangeCredit(0, 0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static ExchangeCredit from(String input) {
        // 허용 입력:
        // - null/blank          -> empty()
        // - "0"                 -> empty() (레거시/디폴트 표기)
        // - "a/b/c/d/e/f/g/h"   -> 8개(레거시, 공통교양 없음)
        // - "a/b/c/d/e/f/g/h/i" -> 9개(신규, 공통교양 포함)
        if (input == null) {
            return ExchangeCredit.empty();
        }
        String trimmed = input.trim();
        if (trimmed.isEmpty() || "0".equals(trimmed)) {
            return ExchangeCredit.empty();
        }

        String[] parts = trimmed.split("/");
        try {
            if (parts.length == 9) {
                int commonCulture = Integer.parseInt(parts[0]);
                int basicAcademicalCulture = Integer.parseInt(parts[1]);
                int normalCulture = Integer.parseInt(parts[2]);
                int major = Integer.parseInt(parts[3]);
                int dualBasicAcademicalCulture = Integer.parseInt(parts[4]);
                int dualMajor = Integer.parseInt(parts[5]);
                int fusionMajor = Integer.parseInt(parts[6]);
                int subMajor = Integer.parseInt(parts[7]);
                int freeElective = Integer.parseInt(parts[8]);
                return new ExchangeCredit(commonCulture, basicAcademicalCulture, normalCulture, major,
                        dualBasicAcademicalCulture, dualMajor, fusionMajor, subMajor, freeElective);
            } else if (parts.length == 8) {
                // 레거시 포맷(공통교양 미포함): [학문기초, 일반교양, 전공, 복수기초, 복수전공, 융합, 부전공, 자유선택]
                int commonCulture = 0;
                int basicAcademicalCulture = Integer.parseInt(parts[0]);
                int normalCulture = Integer.parseInt(parts[1]);
                int major = Integer.parseInt(parts[2]);
                int dualBasicAcademicalCulture = Integer.parseInt(parts[3]);
                int dualMajor = Integer.parseInt(parts[4]);
                int fusionMajor = Integer.parseInt(parts[5]);
                int subMajor = Integer.parseInt(parts[6]);
                int freeElective = Integer.parseInt(parts[7]);
                return new ExchangeCredit(commonCulture, basicAcademicalCulture, normalCulture, major,
                        dualBasicAcademicalCulture, dualMajor, fusionMajor, subMajor, freeElective);
            } else {
                throw new IllegalArgumentException("교환학생 인정학점 형식 오류: 필드 개수=" + parts.length + " (허용: 8 또는 9, 또는 \"0\")");
            }
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("교환학생 인정학점 숫자 파싱 오류: " + trimmed, ex);
        }
    }

    private static boolean isValid(String input) {
        if (input == null) return false;
        // 숫자 8개 (슬래시 7개) 또는 9개 (슬래시 8개) 허용
        return input.matches("^\\d+(/\\d+){7,8}$");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExchangeCredit that = (ExchangeCredit) o;
        return  commonCulture == that.commonCulture &&
                basicAcademicalCulture == that.basicAcademicalCulture &&
                normalCulture == that.normalCulture &&
                major == that.major &&
                dualBasicAcademicalCulture == that.dualBasicAcademicalCulture &&
                dualMajor == that.dualMajor &&
                fusionMajor == that.fusionMajor &&
                subMajor == that.subMajor &&
                freeElective == that.freeElective;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commonCulture, basicAcademicalCulture, normalCulture, major,
                dualBasicAcademicalCulture, dualMajor, fusionMajor, subMajor, freeElective);
    }

    @Override
    public String toString() {
        return  commonCulture + "/"
                + basicAcademicalCulture + "/"
                + normalCulture + "/"
                + major + "/"
                + dualBasicAcademicalCulture + "/"
                + dualMajor + "/"
                + fusionMajor + "/"
                + subMajor + "/"
                + freeElective;
    }
}
