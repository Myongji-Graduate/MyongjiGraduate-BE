package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public enum PopularLectureCategory {
    ALL("전체"),
    BASIC_ACADEMICAL_CULTURE("학문기초교양"),
    CORE_CULTURE("핵심교양"),
    COMMON_CULTURE("공통교양"),
    NORMAL_CULTURE("일반교양"),
    MANDATORY_MAJOR("전공필수"),
    ELECTIVE_MAJOR("전공선택");

    private final String name;

    public static PopularLectureCategory of(String name) {
        return Arrays.stream(PopularLectureCategory.values())
                .filter(category -> Objects.equals(category.getName(), name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리를 찾을 수 없습니다."));
    }

    public boolean checkMandatoryIfSeperatedByMandatoryAndElective() {
        return this == MANDATORY_MAJOR;
    }

}
