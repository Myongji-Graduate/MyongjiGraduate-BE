package com.plzgraduate.myongjigraduatebe.user.domain.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeCredit {

    private int basicAcademicalCulture;  // 학문기초교양
    private int normalCulture;          // 일반교양
    private int major;                  // 전공
    private int dualBasicAcademicalCulture; // 복수전공학문기초교양
    private int dualMajor;              // 복수전공
    private int fusionMajor;            // 융합전공
    private int subMajor;               // 부전공
    private int freeElective;           // 자유선택

    public static ExchangeCredit empty() {
        return new ExchangeCredit(0, 0, 0, 0, 0, 0, 0, 0);
    }

    public static ExchangeCredit from(String input) {
        if (isValid(input)) {
            String[] parts = input.split("/");
            int basicAcademicalCulture = Integer.parseInt(parts[0]);
            int normalCulture = Integer.parseInt(parts[1]);
            int major = Integer.parseInt(parts[2]);
            int dualBasicAcademicalCulture = Integer.parseInt(parts[3]);
            int dualMajor = Integer.parseInt(parts[4]);
            int fusionMajor = Integer.parseInt(parts[5]);
            int subMajor = Integer.parseInt(parts[6]);
            int freeElective = Integer.parseInt(parts[7]);

            return new ExchangeCredit(basicAcademicalCulture, normalCulture, major,
                    dualBasicAcademicalCulture, dualMajor, fusionMajor, subMajor, freeElective);
        } else {
            throw new IllegalArgumentException("잘못된 형식입니다.");
        }
    }

    private static boolean isValid(String input) {
        // 8개의 숫자가 '/'로 구분된 형식인지 확인
        return input.matches("^\\d+/\\d+/\\d+/\\d+/\\d+/\\d+/\\d+/\\d+$");
    }

    public boolean hasExchange() {
        return basicAcademicalCulture != 0 || normalCulture != 0 || major != 0 ||
                dualBasicAcademicalCulture != 0 || dualMajor != 0 || fusionMajor != 0 ||
                subMajor != 0 || freeElective != 0;
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
        return basicAcademicalCulture == that.basicAcademicalCulture &&
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
        return Objects.hash(basicAcademicalCulture, normalCulture, major,
                dualBasicAcademicalCulture, dualMajor, fusionMajor, subMajor, freeElective);
    }

    @Override
    public String toString() {
        return basicAcademicalCulture + "/"
                + normalCulture + "/"
                + major + "/"
                + dualBasicAcademicalCulture + "/"
                + dualMajor + "/"
                + fusionMajor + "/"
                + subMajor + "/"
                + freeElective;
    }
}
