package com.plzgraduate.myongjigraduatebe.timetable.api.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ReplaceLecturesRequest {
    private int year;
    private int semester;
    private List<Long> timetableIds;
}
