package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import java.util.Objects;
import lombok.Builder;
import lombok.Getter;

@Getter
public class Lecture {

    private static final String CULTURE_CODE_START_PREFIX = "KM";

    private final String id;
    private final String name;
    private final int credit;
    private final String duplicateCode;
    private final int isRevoked;

    @Builder
    private Lecture(String id, String name, int credit, int isRevoked,
            String duplicateCode) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.isRevoked = isRevoked;
        this.duplicateCode = duplicateCode;
    }

    public static Lecture from(String lectureCode) {
        return Lecture.builder()
                .id(lectureCode)
                .build();
    }

    public static Lecture of(String lectureCode, String name, int credit, int isRevoked,
            String duplicateCode) {
        return Lecture.builder()
                .id(lectureCode)
                .name(name)
                .credit(credit)
                .isRevoked(isRevoked)
                .duplicateCode(duplicateCode)
                .build();
    }

    public boolean isCulture() {
        return id.startsWith(CULTURE_CODE_START_PREFIX);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Lecture lecture = (Lecture) o;
        return Objects.equals(id, lecture.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
