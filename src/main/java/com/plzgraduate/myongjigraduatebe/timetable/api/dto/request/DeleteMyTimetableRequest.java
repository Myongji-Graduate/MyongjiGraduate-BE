package com.plzgraduate.myongjigraduatebe.timetable.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DeleteMyTimetableRequest {
    private int year;
    private int semester;
}
