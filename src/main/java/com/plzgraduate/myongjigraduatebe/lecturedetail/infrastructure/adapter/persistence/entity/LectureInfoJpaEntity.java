package com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "lecture_information")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureInfoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private String professor;

    private String assignment;

    private String attendance;

    private String exam;

    private String grading;

    private String teamwork;

    private BigDecimal rating;

    @Builder
    public LectureInfoJpaEntity(
            Long id,
            String subject,
            String professor,
            String assignment,
            String attendance,
            String exam,
            String grading,
            String teamwork,
            BigDecimal rating
    ) {
        this.id = id;
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
