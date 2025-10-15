package com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LectureInfo {

    private final String subject;
    private final String professor;
    private final String assignment;
    private final String attendance;
    private final String exam;
    private final String grading;
    private final String teamwork;
    private final BigDecimal rating;

    @Builder
    private LectureInfo(
            String subject,
            String professor,
            String assignment,
            String attendance,
            String exam,
            String grading,
            String teamwork,
            BigDecimal rating
    ) {
        this.subject = subject;
        this.professor = professor;
        this.assignment = assignment;
        this.attendance = attendance;
        this.exam = exam;
        this.grading = grading;
        this.teamwork = teamwork;
        this.rating = rating;
    }
}
