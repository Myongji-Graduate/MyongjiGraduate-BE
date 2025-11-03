package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "major_lecture_offering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MajorLectureOfferingJpaEntity {

    @Id
    @Column(name = "lecture_id")
    private String lectureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private LectureJpaEntity lecture;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "offered_semester")
    private Integer offeredSemester;

    public void setLecture(LectureJpaEntity lecture) {
        this.lecture = lecture;
        this.lectureId = (lecture != null) ? lecture.getId() : null;
    }

    @Builder
    private MajorLectureOfferingJpaEntity(String lectureId, Integer grade, Integer offeredSemester) {
        this.lectureId = lectureId;
        this.grade = grade;
        this.offeredSemester = offeredSemester;
    }
}