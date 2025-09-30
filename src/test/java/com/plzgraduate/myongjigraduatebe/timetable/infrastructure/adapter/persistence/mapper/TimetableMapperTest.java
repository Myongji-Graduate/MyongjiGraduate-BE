package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TimetableMapperTest {

    private final TimetableMapper mapper = new TimetableMapper();

    @Test
    @DisplayName("mapToDomainEntity: 모든 필드가 올바르게 매핑된다")
    void mapToDomainEntity_mapsAllFields() {
        // given
        TimetableJpaEntity entity = TimetableJpaEntity.builder()
                .id(1L)
                .year(2025)
                .semester(2)
                .maxStudent(50)
                .koreanCode("인소102")
                .lectureCode("KMA00001")
                .classDivision("0001")
                .name("알고리즘")
                .credit(3)
                .campus("인문")
                .department("컴퓨터공학과")
                .professor("홍길동")
                .day1("월")
                .time1("0900-1045")
                .lectureRoom("Y2508")
                .day2("수")
                .time2("0900-1045")
                .note("비고")
                .startMinute1(9 * 60)
                .endMinute1(10 * 60 + 45)
                .startMinute2(9 * 60)
                .endMinute2(10 * 60 + 45)
                .build();

        // when
        Timetable domain = mapper.mapToDomainEntity(entity);

        // then
        assertThat(domain.getId()).isEqualTo(1L);
        assertThat(domain.getYear()).isEqualTo(2025);
        assertThat(domain.getSemester()).isEqualTo(2);
        assertThat(domain.getMaxStudent()).isEqualTo(50);
        assertThat(domain.getKoreanCode()).isEqualTo("인소102");
        assertThat(domain.getLectureCode()).isEqualTo("KMA00001");
        assertThat(domain.getClassDivision()).isEqualTo("0001");
        assertThat(domain.getName()).isEqualTo("알고리즘");
        assertThat(domain.getCredit()).isEqualTo(3);
        assertThat(domain.getCampus()).isEqualTo("인문");
        assertThat(domain.getDepartment()).isEqualTo("컴퓨터공학과");
        assertThat(domain.getProfessor()).isEqualTo("홍길동");
        assertThat(domain.getDay1()).isEqualTo("월");
        assertThat(domain.getTime1()).isEqualTo("0900-1045");
        assertThat(domain.getLectureRoom()).isEqualTo("Y2508");
        assertThat(domain.getDay2()).isEqualTo("수");
        assertThat(domain.getTime2()).isEqualTo("0900-1045");
        assertThat(domain.getNote()).isEqualTo("비고");
        assertThat(domain.getStartMinute1()).isEqualTo(9 * 60);
        assertThat(domain.getEndMinute1()).isEqualTo(10 * 60 + 45);
        assertThat(domain.getStartMinute2()).isEqualTo(9 * 60);
        assertThat(domain.getEndMinute2()).isEqualTo(10 * 60 + 45);
    }

    @Test
    @DisplayName("mapToDomainEntity: 선택 필드가 null이어도 NPE 없이 매핑된다")
    void mapToDomainEntity_handlesNullOptionals() {
        // given - optional 필드들을 null로 설정
        TimetableJpaEntity entity = TimetableJpaEntity.builder()
                .id(2L)
                .year(2024)
                .semester(1)
                .maxStudent(40)
                .koreanCode("인소102")
                .lectureCode("KMA12345")
                .classDivision("0002")
                .name("자료구조")
                .credit(3)
                .campus("자연")
                .department("소프트웨어학과")
                .professor(null)
                .day1(null)
                .time1(null)
                .lectureRoom(null)
                .day2(null)
                .time2(null)
                .note(null)
                .startMinute1(null)
                .endMinute1(null)
                .startMinute2(null)
                .endMinute2(null)
                .build();

        // when
        Timetable domain = mapper.mapToDomainEntity(entity);

        // then
        assertThat(domain.getId()).isEqualTo(2L);
        assertThat(domain.getYear()).isEqualTo(2024);
        assertThat(domain.getSemester()).isEqualTo(1);
        assertThat(domain.getMaxStudent()).isEqualTo(40);
        assertThat(domain.getKoreanCode()).isEqualTo("인소102");
        assertThat(domain.getLectureCode()).isEqualTo("KMA12345");
        assertThat(domain.getClassDivision()).isEqualTo("0002");
        assertThat(domain.getName()).isEqualTo("자료구조");
        assertThat(domain.getCredit()).isEqualTo(3);
        assertThat(domain.getCampus()).isEqualTo("자연");
        assertThat(domain.getDepartment()).isEqualTo("소프트웨어학과");

        // null 허용 필드 확인
        assertThat(domain.getProfessor()).isNull();
        assertThat(domain.getDay1()).isNull();
        assertThat(domain.getTime1()).isNull();
        assertThat(domain.getLectureRoom()).isNull();
        assertThat(domain.getDay2()).isNull();
        assertThat(domain.getTime2()).isNull();
        assertThat(domain.getNote()).isNull();
        assertThat(domain.getStartMinute1()).isNull();
        assertThat(domain.getEndMinute1()).isNull();
        assertThat(domain.getStartMinute2()).isNull();
        assertThat(domain.getEndMinute2()).isNull();
    }
}