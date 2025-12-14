package com.plzgraduate.myongjigraduatebe.timetable.api.dto.request;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.recommend.FreeDay;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendBeforeTimetableRequest {
    private int year;
    private int semester;
    @Schema(description = "목표 학점")
    private int targetCredits;

    @Schema(description = "공강 요일 선택 (여러 개 선택 가능)")
    private List<FreeDay> freeDays;
}
