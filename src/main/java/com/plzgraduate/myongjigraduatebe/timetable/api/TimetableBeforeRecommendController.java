package com.plzgraduate.myongjigraduatebe.timetable.api;

import com.plzgraduate.myongjigraduatebe.core.meta.LoginUser;
import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.RecommendBeforeTimetableRequest;
import io.swagger.v3.oas.annotations.Parameter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendBeforeTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.RecommendBeforeTimetableUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@WebAdapter
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableBeforeRecommendController {
    private final RecommendBeforeTimetableUseCase useCase;

    @PostMapping("/recommend-before")
    public RecommendBeforeTimetableResponse recommendBefore(@Parameter(hidden = true) @LoginUser Long userId,
                                                            @RequestBody @Valid RecommendBeforeTimetableRequest req) {
        return useCase.recommend(userId, req.getTargetCredits(), req.getFreeDays(), req.getYear(), req.getSemester());
    }
}
