package com.plzgraduate.myongjigraduatebe.timetable.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimetableTest {

    @Test
    @DisplayName("Builder로 모든 필드를 세팅하면 동일한 값이 노출된다")
    void builder_setsAllFields() {
        Timetable tt = Timetable.builder()
                .id(10L)
                .year(2025)
                .semester(2)
                .maxStudent(60)
                .koreanCode("인소102")
                .lectureCode("KMA12345")
                .classDivision("0001")
                .name("알고리즘")
                .credit(3)
                .campus("인문")
                .department("컴퓨터공학과")
                .professor("홍길동")
                .day1("월")
                .time1("0900-1050")
                .lectureRoom("Y2508")
                .day2("수")
                .time2("0900-0950")
                .note("비고")
                .startMinute1(9 * 60)
                .endMinute1(10 * 60 + 50)
                .startMinute2(9 * 60)
                .endMinute2(9 * 60 + 50)
                .build();

        assertThat(tt.getId()).isEqualTo(10L);
        assertThat(tt.getYear()).isEqualTo(2025);
        assertThat(tt.getSemester()).isEqualTo(2);
        assertThat(tt.getMaxStudent()).isEqualTo(60);
        assertThat(tt.getKoreanCode()).isEqualTo("인소102");
        assertThat(tt.getLectureCode()).isEqualTo("KMA12345");
        assertThat(tt.getClassDivision()).isEqualTo("0001");
        assertThat(tt.getName()).isEqualTo("알고리즘");
        assertThat(tt.getCredit()).isEqualTo(3);
        assertThat(tt.getCampus()).isEqualTo("인문");
        assertThat(tt.getDepartment()).isEqualTo("컴퓨터공학과");
        assertThat(tt.getProfessor()).isEqualTo("홍길동");
        assertThat(tt.getDay1()).isEqualTo("월");
        assertThat(tt.getTime1()).isEqualTo("0900-1050");
        assertThat(tt.getLectureRoom()).isEqualTo("Y2508");
        assertThat(tt.getDay2()).isEqualTo("수");
        assertThat(tt.getTime2()).isEqualTo("0900-0950");
        assertThat(tt.getNote()).isEqualTo("비고");
        assertThat(tt.getStartMinute1()).isEqualTo(540);
        assertThat(tt.getEndMinute1()).isEqualTo(650);
        assertThat(tt.getStartMinute2()).isEqualTo(540);
        assertThat(tt.getEndMinute2()).isEqualTo(590);
    }

    @Test
    @DisplayName("nullable 필드는 null로 세팅해도 안전하게 노출된다")
    void builder_allowsNullableFields() {
        Timetable tt = Timetable.builder()
                .id(1L)
                .year(2024)
                .semester(1)
                .maxStudent(50)
                .koreanCode("인소102")
                .lectureCode("KMA00001")
                .classDivision("0002")
                .name("프로그래밍입문")
                .credit(3)
                .campus("자연")
                .department("소프트웨어학과")
                .professor(null)   // nullable
                .day1("화")
                .time1("1000-1150")
                .lectureRoom(null) // nullable
                .day2(null)        // nullable
                .time2(null)       // nullable
                .note(null)        // nullable
                .startMinute1(null)
                .endMinute1(null)
                .startMinute2(null)
                .endMinute2(null)
                .build();

        assertThat(tt.getProfessor()).isNull();
        assertThat(tt.getLectureRoom()).isNull();
        assertThat(tt.getDay2()).isNull();
        assertThat(tt.getTime2()).isNull();
        assertThat(tt.getNote()).isNull();
        assertThat(tt.getStartMinute1()).isNull();
        assertThat(tt.getEndMinute1()).isNull();
        assertThat(tt.getStartMinute2()).isNull();
        assertThat(tt.getEndMinute2()).isNull();

        // 필수적으로 넣은 값들은 정상 유지
        assertThat(tt.getYear()).isEqualTo(2024);
        assertThat(tt.getSemester()).isEqualTo(1);
        assertThat(tt.getLectureCode()).isEqualTo("KMA00001");
    }
}