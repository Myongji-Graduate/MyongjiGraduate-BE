package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class TimetableMapper {

    public Timetable mapToDomainEntity(TimetableJpaEntity entity) {
        return Timetable.builder()
                .id(entity.getId())
                .year(entity.getYear())
                .semester(entity.getSemester())
                .maxStudent(entity.getMaxStudent())
                .koreanCode(entity.getKoreanCode())
                .lectureCode(entity.getLectureCode())
                .classDivision(entity.getClassDivision())
                .name(entity.getName())
                .credit(entity.getCredit())
                .campus(entity.getCampus())
                .department(entity.getDepartment())
                .professor(entity.getProfessor())
                .day1(entity.getDay1())
                .time1(entity.getTime1())
                .lectureRoom(entity.getLectureRoom())
                .day2(entity.getDay2())
                .time2(entity.getTime2())
                .note(entity.getNote())
                .startMinute1(entity.getStartMinute1())
                .endMinute1(entity.getEndMinute1())
                .startMinute2(entity.getStartMinute2())
                .endMinute2(entity.getEndMinute2())
                .build();
    }
}