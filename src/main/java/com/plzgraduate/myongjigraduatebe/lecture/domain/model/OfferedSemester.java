package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Getter;

@Getter
public enum OfferedSemester {
    FIRST(1),   // 1학기
    SECOND(2),  // 2학기
    BOTH(3);    // 양 학기

    private final int code;

    OfferedSemester(int code) { this.code = code; }

    public static OfferedSemester of(int code) {
        switch (code) {
            case 1:
                return FIRST;
            case 2:
                return SECOND;
            case 3:
                return BOTH;
            default:
                throw new IllegalArgumentException("Invalid offeredSemester code: " + code);
        }
    }
}

