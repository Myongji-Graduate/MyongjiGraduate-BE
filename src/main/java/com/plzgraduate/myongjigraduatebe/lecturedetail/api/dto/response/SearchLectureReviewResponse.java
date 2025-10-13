package com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SearchLectureReviewResponse {

    private final String subject;
    private final String professor;
    private final String semester;
    private final BigDecimal rating;
    private final String content;

    @Builder
    private SearchLectureReviewResponse(String subject, String professor, String semester, BigDecimal rating, String content) {
        this.subject = subject;
        this.professor = professor;
        this.semester = semester;
        this.rating = rating;
        this.content = content;
    }

    public static SearchLectureReviewResponse from(LectureReview lectureReview) {
        return SearchLectureReviewResponse.builder()
                .subject(lectureReview.getSubject())
                .professor(lectureReview.getProfessor())
                .semester(lectureReview.getSemester())
                .rating(lectureReview.getRating())
                .content(lectureReview.getContent())
                .build();
    }
}
