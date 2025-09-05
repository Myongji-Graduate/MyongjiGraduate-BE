package com.plzgraduate.myongjigraduatebe.timetable.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimetableSearchConditionRequest {
    private String keyword;
    private String department;
    private String professor;
    private String campus;
}
