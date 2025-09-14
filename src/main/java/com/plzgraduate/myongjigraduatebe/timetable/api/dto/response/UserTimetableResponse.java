package com.plzgraduate.myongjigraduatebe.timetable.api.dto.response;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserTimetableResponse {
    private Long id;
    private Long timetableId;
    private int year;
    private int semester;

    public static UserTimetableResponse from(UserTimetable userTimetable) {
        return new UserTimetableResponse(
                userTimetable.getId(),
                userTimetable.getTimetableId(),
                userTimetable.getYear(),
                userTimetable.getSemester()
        );
    }
}