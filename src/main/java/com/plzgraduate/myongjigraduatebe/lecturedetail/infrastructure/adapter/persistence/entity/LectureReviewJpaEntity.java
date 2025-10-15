package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lecture_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureReviewJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String professor;

    private String semester;

    private BigDecimal rating;

    private String content;

    @Builder
    public LectureReviewJpaEntity(
            Long id,
            String subject,
            String professor,
            String semester,
            BigDecimal rating,
            String content
    ) {
        this.id = id;
        this.subject = subject;
        this.professor = professor;
        this.semester = semester;
        this.rating = rating;
        this.content = content;
    }
}
