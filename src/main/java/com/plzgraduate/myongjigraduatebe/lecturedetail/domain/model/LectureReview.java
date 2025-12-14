package com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class LectureReview {

    private final String subject;
    private final String professor;
    private final String semester;
    private final BigDecimal rating;
    private final String content;

    @Builder
    private LectureReview(
            String subject,
            String professor,
            String semester,
            BigDecimal rating,
            String content
    ) {
        this.subject = subject;
        this.professor = professor;
        this.semester = semester;
        this.rating = rating;
        this.content = content;
    }

    public static LectureReview of(
            String subject,
            String professor,
            String semester,
            java.math.BigDecimal rating,
            String content
    ) {
        return new LectureReview(subject, professor, semester, rating, content);
    }
}
