package com.plzgraduate.myongjigraduatebe.log.domain;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class InvalidTakenLectureLog {
    private final String studentNumber;
    private final String lectureCode;
    private final String lectureName;
    private final int year;
    private final int semester;
    private final LocalDateTime timestamp;

    @Builder
    public InvalidTakenLectureLog(String studentNumber, String lectureCode, String lectureName, int year, int semester, LocalDateTime timestamp) {
        this.studentNumber = studentNumber;
        this.lectureCode = lectureCode;
        this.lectureName = lectureName;
        this.year = year;
        this.semester = semester;
        this.timestamp = timestamp;
    }
}
