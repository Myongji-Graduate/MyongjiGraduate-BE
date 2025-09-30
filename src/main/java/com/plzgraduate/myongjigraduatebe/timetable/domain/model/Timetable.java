package com.plzgraduate.myongjigraduatebe.timetable.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Timetable {

    private final Long id;
    private final int year;
    private final int semester;
    private final int maxStudent;
    private final String koreanCode;
    private final String lectureCode;
    private final String classDivision;
    private final String name;
    private final int credit;
    private final String campus;
    private final String department;
    private final String professor;
    private final String day1;
    private final String time1;
    private final String lectureRoom;
    private final String day2;
    private final String time2;
    private final String note;

    private final Integer startMinute1;
    private final Integer endMinute1;
    private final Integer startMinute2;
    private final Integer endMinute2;

    @Builder
    public Timetable(
            Long id,
            int year,
            int semester, int maxStudent, String koreanCode,
            String lectureCode,
            String classDivision,
            String name,
            int credit,
            String campus,
            String department,
            String professor,
            String day1,
            String time1,
            String lectureRoom,
            String day2,
            String time2,
            String note,
            Integer startMinute1,
            Integer endMinute1,
            Integer startMinute2,
            Integer endMinute2
    ) {

        this.id = id;
        this.year = year;
        this.semester = semester;
        this.maxStudent = maxStudent;
        this.koreanCode = koreanCode;
        this.lectureCode = lectureCode;
        this.classDivision = classDivision;
        this.name = name;
        this.credit = credit;
        this.campus = campus;
        this.department = department;
        this.professor = professor;
        this.day1 = day1;
        this.time1 = time1;
        this.lectureRoom = lectureRoom;
        this.day2 = day2;
        this.time2 = time2;
        this.note = note;
        this.startMinute1 = startMinute1;
        this.endMinute1 = endMinute1;
        this.startMinute2 = startMinute2;
        this.endMinute2 = endMinute2;
    }
}