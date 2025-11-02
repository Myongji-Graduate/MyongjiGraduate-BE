package com.plzgraduate.myongjigraduatebe.timetable.application.usecase;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendAfterTimetableResponse;

public interface RecommendAfterTimetableUseCase {
    RecommendAfterTimetableResponse build(Long userId);
}
