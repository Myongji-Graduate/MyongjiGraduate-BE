package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTimetableMapperTest {

    private final UserTimetableMapper mapper = new UserTimetableMapper();

    @Test
    @DisplayName("mapToDomainEntity: JPA → Domain 필드 매핑")
    void mapToDomainEntity_mapsAllFields() {
        // given: JPA 엔티티들 (영속성 필요 없음)
        UserJpaEntity user = UserJpaEntity.builder()
                .id(7L)
                .authId("auth7")
                .password("pw")
                .studentNumber("60190001")
                .build();

        TimetableJpaEntity timetable = TimetableJpaEntity.builder()
                .id(11L)
                .build();

        UserTimetableJpaEntity entity = UserTimetableJpaEntity.builder()
                .id(33L)
                .user(user)
                .timetable(timetable)
                .year(2025)
                .semester(2)
                .build();

        // when
        UserTimetable domain = mapper.mapToDomainEntity(entity);

        // then
        assertAll(
                () -> assertEquals(33L, domain.getId()),
                () -> assertEquals(7L, domain.getUserId()),
                () -> assertEquals(11L, domain.getTimetableId()),
                () -> assertEquals(2025, domain.getYear()),
                () -> assertEquals(2, domain.getSemester())
        );
    }

    @Test
    @DisplayName("mapToJpaEntity: Domain → JPA 필드 매핑")
    void mapToJpaEntity_mapsAllFields() {
        // given: 도메인과 JPA 레퍼런스
        UserTimetable domain = UserTimetable.builder()
                .id(44L)
                .userId(8L)
                .timetableId(22L)
                .year(2024)
                .semester(1)
                .build();

        UserJpaEntity userRef = UserJpaEntity.builder()
                .id(8L)
                .authId("auth8")
                .password("pw")
                .studentNumber("60190002")
                .build();

        TimetableJpaEntity timetableRef = TimetableJpaEntity.builder()
                .id(22L)
                .build();

        // when
        UserTimetableJpaEntity jpa = mapper.mapToJpaEntity(domain, userRef, timetableRef);

        // then
        assertAll(
                () -> assertEquals(44L, jpa.getId()),
                () -> assertEquals(8L, jpa.getUser().getId()),
                () -> assertEquals(22L, jpa.getTimetable().getId()),
                () -> assertEquals(2024, jpa.getYear()),
                () -> assertEquals(1, jpa.getSemester())
        );
    }
}