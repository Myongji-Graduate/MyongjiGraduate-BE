package com.plzgraduate.myongjigraduatebe.lecture.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class MajorLectureOffering {

    private final String lectureId;
    private final int grade;
    private final OfferedSemester semester;

    public MajorLectureOffering(String lectureId, int grade, OfferedSemester semester) {
        if (lectureId == null || lectureId.isBlank()) {
            throw new IllegalArgumentException("lectureId must not be null/blank");
        }
        if (grade < 0 || grade > 4) {
            throw new IllegalArgumentException("grade must be 0~4 (0 = 전학년 공통)");
        }
        this.lectureId = lectureId;
        this.grade = grade;
        this.semester = semester;
    }
    public static MajorLectureOffering of(String lectureId, int grade, OfferedSemester semester) {
        return new MajorLectureOffering(lectureId, grade, semester);
    }

    public static MajorLectureOffering ofCode(String lectureId, int grade, int offeredSemesterCode) {
        return new MajorLectureOffering(lectureId, grade, OfferedSemester.of(offeredSemesterCode));
    }

    public boolean isOfferedInSemester(int semesterCode) {
        return semester == OfferedSemester.BOTH
                || (semesterCode == 1 && semester == OfferedSemester.FIRST)
                || (semesterCode == 2 && semester == OfferedSemester.SECOND);
    }

    public boolean matchesGrade(int g) {
        return this.grade == 0 || this.grade == g; // 0은 전학년 공통
    }

    public int getOfferedSemesterCode() {
        return semester.getCode();
    }
}
