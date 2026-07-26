package com.plzgraduate.myongjigraduatebe.timetable.api.dto.request;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.recommend.FreeDay;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class RecommendBeforeTimetableRequest {
    @Min(value = 2020, message = "개설 연도는 2020년 이상이어야 합니다.")
    private int year;

    @Min(value = 1, message = "학기는 1 또는 2여야 합니다.")
    @Max(value = 2, message = "학기는 1 또는 2여야 합니다.")
    private int semester;

    @Schema(description = "목표 학점")
    @Min(value = 1, message = "목표 학점은 1학점 이상이어야 합니다.")
    @Max(value = 21, message = "목표 학점은 21학점 이하여야 합니다.")
    private int targetCredits;

    @Schema(description = "공강 요일 선택 (여러 개 선택 가능)")
    @NotNull(message = "공강 요일 목록은 null일 수 없습니다.")
    private List<FreeDay> freeDays = new ArrayList<>();
}
