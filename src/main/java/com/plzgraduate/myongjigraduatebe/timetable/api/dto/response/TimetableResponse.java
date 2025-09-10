package com.plzgraduate.myongjigraduatebe.timetable.api.dto.response;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import lombok.Builder;
import lombok.Getter;
import io.swagger.v3.oas.annotations.media.Schema;

@Getter
public class TimetableResponse {

    @Schema(name = "id", example = "5976", description = "식별 아이디")
    private final Long id;

    @Schema(name = "lectureCode", example = "HEF01102", description = "교과목 코드")
    private final String lectureCode;

    @Schema(name = "name", example = "기초프로그래밍2", description = "교과목명")
    private final String name;

    @Schema(name = "credit", example = "3", description = "학점")
    private final int credit;

    @Schema(name = "campus", example = "인문", description = "캠퍼스")
    private final String campus;

    @Schema(name = "year", example = "2025", description = "개설 연도")
    private final int year;

    @Schema(name = "semester", example = "2", description = "개설 학기 (1: 1학기, 2: 2학기)")
    private final int semester;

    @Schema(name = "maxStudent", example = "50", description = "최대 인원")
    private final String maxStudent;

    @Schema(name = "koreanCode", example = "인소102", description = "한글 코드")
    private final String koreanCode;

    @Schema(name = "department", example = "인공지능·소프트웨어융합대학", description = "개설 학과")
    private final String department;

    @Schema(name = "professor", example = "정재희", description = "교수명")
    private final String professor;

    @Schema(name = "day1", example = "화요일", description = "첫 번째 수업 요일")
    private final String day1;

    @Schema(name = "time1", example = "1030 - 1145", description = "첫 번째 수업 시간")
    private final String time1;

    @Schema(name = "startMinute1", example = "630", description = "첫 번째 수업 시작 시간 (분 단위)")
    private final Integer startMinute1;

    @Schema(name = "endMinute1", example = "705", description = "첫 번째 수업 종료 시간 (분 단위)")
    private final Integer endMinute1;

    @Schema(name = "day2", example = "목요일", description = "두 번째 수업 요일")
    private final String day2;

    @Schema(name = "time2", example = "1030 - 1145", description = "두 번째 수업 시간")
    private final String time2;

    @Schema(name = "startMinute2", example = "630", description = "두 번째 수업 시작 시간 (분 단위)")
    private final Integer startMinute2;

    @Schema(name = "endMinute2", example = "705", description = "두 번째 수업 종료 시간 (분 단위)")
    private final Integer endMinute2;

    @Schema(name = "lectureRoom", example = "S1558", description = "강의실")
    private final String lectureRoom;

    @Schema(name = "note", example = "*인공지능·소프트웨어융합대학 전공이해기초교과", description = "비고")
    private final String note;

    @Builder
    public TimetableResponse(Long id, String lectureCode, String name, int credit,
                             String campus, int year, int semester, String maxStudent,
                             String koreanCode, String department, String professor,
                             String day1, String time1, Integer startMinute1, Integer endMinute1,
                             String day2, String time2, Integer startMinute2, Integer endMinute2,
                             String lectureRoom, String note) {
        this.id = id;
        this.lectureCode = lectureCode;
        this.name = name;
        this.credit = credit;
        this.campus = campus;
        this.year = year;
        this.semester = semester;
        this.maxStudent = maxStudent;
        this.koreanCode = koreanCode;
        this.department = department;
        this.professor = professor;
        this.day1 = day1;
        this.time1 = time1;
        this.startMinute1 = startMinute1;
        this.endMinute1 = endMinute1;
        this.day2 = day2;
        this.time2 = time2;
        this.startMinute2 = startMinute2;
        this.endMinute2 = endMinute2;
        this.lectureRoom = lectureRoom;
        this.note = note;
    }
    public static TimetableResponse from(Timetable timetable) {
        return TimetableResponse.builder()
                .id(timetable.getId())
                .lectureCode(timetable.getLectureCode())
                .name(timetable.getName())
                .credit(timetable.getCredit())
                .campus(timetable.getCampus())
                .year(timetable.getYear())
                .semester(timetable.getSemester())
                .maxStudent(String.valueOf(timetable.getMaxStudent()))
                .koreanCode(timetable.getKoreanCode())
                .department(timetable.getDepartment())
                .professor(timetable.getProfessor())
                .day1(timetable.getDay1())
                .time1(timetable.getTime1())
                .startMinute1(timetable.getStartMinute1())
                .endMinute1(timetable.getEndMinute1())
                .day2(timetable.getDay2())
                .time2(timetable.getTime2())
                .startMinute2(timetable.getStartMinute2())
                .endMinute2(timetable.getEndMinute2())
                .lectureRoom(timetable.getLectureRoom())
                .note(timetable.getNote())
                .build();
    }
}