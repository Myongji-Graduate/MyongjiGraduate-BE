package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureReview;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.LectureReviewJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class LectureDetailMapper {

    public LectureReview mapToLectureReviewModel(LectureReviewJpaEntity lectureReview) {
        return LectureReview.builder()
                .subject(lectureReview.getSubject())
                .professor(lectureReview.getProfessor())
                .semester(lectureReview.getSemester())
                .rating(lectureReview.getRating())
                .content(lectureReview.getContent())
                .build();
    }
}
