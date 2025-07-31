package com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.entity;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "invalid_taken_lecture_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class InvalidTakenLectureLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String studentNumber;
    private String lectureCode;
    private String lectureName;
    private int year;
    private int semester;
    private LocalDateTime timestamp;
}