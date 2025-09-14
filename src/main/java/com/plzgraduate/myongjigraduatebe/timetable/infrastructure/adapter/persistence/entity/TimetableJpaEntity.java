package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity;


import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(
    name = "timetables",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"year", "semester", "lectureCode", "classDivision"})
    }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class TimetableJpaEntity {

    /**
     * 내부 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 분반 / 강좌 번호 (예: 0001)
     */
    @Column(nullable = false)
    private String classDivision;

    /**
     * 학수번호 / 교과목 코드 (예: KMA00101)
     */
    @Column(nullable = false)
    private String lectureCode;

    /**
     * 교과목명
     */
    @Column(nullable = false)
    private String name;

    /**
     * 학점
     */
    @Column(nullable = false)
    private int credit;

    /**
     * 캠퍼스 구분
     */
    @Column(nullable = false)
    private String campus;

    /**
     * 개설 연도 (예: 2025)
     */
    @Column(nullable = false)
    private int year;

    /**
     * 개설 학기 (1: 1학기, 2: 2학기)
     */
    @Column(nullable = false)
    private int semester;

    /**
     * 제한 인원
     */
    @Column(nullable = false)
    private int maxStudent;

    /**
     * 한글 코드
     */
    @Column(nullable = false)
    private String koreanCode;

    /**
     * 개설 학과 또는 단과대
     */
    @Column(nullable = false)
    private String department;

    /**
     * 교수명
     */
    private String professor;

    /**
     * 첫 번째 수업 요일 (예: 월요일)
     */
    private String day1;
    /**
     * 첫 번째 수업 시간 (예: 0900 - 1050)
     */
    private String time1;
    /**
     * 강의실 정보 (예: Y2508)
     */
    private String lectureRoom;
    /**
     * 두 번째 수업 요일 (선택적)
     */
    private String day2;
    /**
     * 두 번째 수업 시간 (선택적)
     */
    private String time2;
    /**
     * 기타 비고 (선택적)
     */
    private String note;

    // 시간표용 minute
    private Integer startMinute1;

    private Integer endMinute1;

    private Integer startMinute2;

    private Integer endMinute2;
}
