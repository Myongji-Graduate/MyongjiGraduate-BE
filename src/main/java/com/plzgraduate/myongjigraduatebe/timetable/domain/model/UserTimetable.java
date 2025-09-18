package com.plzgraduate.myongjigraduatebe.timetable.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserTimetable {
    private Long id;
    private Long userId;
    private Long timetableId;
    private int year;
    private int semester;
}
