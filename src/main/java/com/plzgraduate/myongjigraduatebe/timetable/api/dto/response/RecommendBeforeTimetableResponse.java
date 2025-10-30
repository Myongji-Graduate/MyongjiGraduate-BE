package com.plzgraduate.myongjigraduatebe.timetable.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecommendBeforeTimetableResponse {
    @Schema(description = "추천된 총 학점")
    private int totalCredits;

    @Schema(description = "추천된 시간표 과목 목록")
    private List<TimetableResponse> lectures;

}
