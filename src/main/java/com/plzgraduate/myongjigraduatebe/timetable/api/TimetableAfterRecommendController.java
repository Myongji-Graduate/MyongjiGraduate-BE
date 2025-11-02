package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendAfterTimetableResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.RecommendAfterTimetableUseCase;

@WebAdapter
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableAfterRecommendController {
    private final RecommendAfterTimetableUseCase recommendAfterTimetableUseCase;

    @GetMapping("/recommend-after")
    public RecommendAfterTimetableResponse recommendAfterTimetable(@Parameter(hidden = true) @LoginUser
    Long userId) {
        return recommendAfterTimetableUseCase.build(userId);
    }

}
