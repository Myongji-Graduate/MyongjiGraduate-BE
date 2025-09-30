package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class UserTimetableMapper {

    public UserTimetable mapToDomainEntity(UserTimetableJpaEntity entity) {
        return UserTimetable.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .timetableId(entity.getTimetable().getId())
                .year(entity.getYear())
                .semester(entity.getSemester())
                .build();
    }

    public UserTimetableJpaEntity mapToJpaEntity(UserTimetable domain, UserJpaEntity user, TimetableJpaEntity timetable) {
        return UserTimetableJpaEntity.builder()
                .id(domain.getId())
                .user(user)
                .timetable(timetable)
                .year(domain.getYear())
                .semester(domain.getSemester())
                .build();
    }
}