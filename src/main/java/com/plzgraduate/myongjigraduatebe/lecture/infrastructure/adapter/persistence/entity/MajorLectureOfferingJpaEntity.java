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

    // 🔁 기존: MajorLectureJpaEntity ← 잘못된 연관 (PK 타입 불일치 유발)
    // @OneToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "lecture_id", referencedColumnName = "lecture_id",
    //         insertable = false, updatable = false)
    // private MajorLectureJpaEntity major;

    // ✅ 변경: lecture_id → lecture(id, varchar) 에 대한 FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id", referencedColumnName = "id",
            insertable = false, updatable = false)
    private LectureJpaEntity lecture;

    @Column(name = "grade")
    private Integer grade;

    @Column(name = "offered_semester")
    private Integer offeredSemester;

    // 신규: Lecture 연관 설정 시 lectureId 동기화
    public void setLecture(LectureJpaEntity lecture) {
        this.lecture = lecture;
        this.lectureId = (lecture != null) ? lecture.getId() : null;
    }

    /**
     * @deprecated 과거 호출 호환용. 가능하면 setLecture(LectureJpaEntity)를 사용하세요.
     */
    @Deprecated
    public void setMajor(MajorLectureJpaEntity major) {
        this.lecture = (major != null) ? major.getLectureJpaEntity() : null;
        this.lectureId = (this.lecture != null) ? this.lecture.getId() : null;
    }

    @Builder
    private MajorLectureOfferingJpaEntity(String lectureId, Integer grade, Integer offeredSemester) {
        this.lectureId = lectureId;
        this.grade = grade;
        this.offeredSemester = offeredSemester;
    }
}