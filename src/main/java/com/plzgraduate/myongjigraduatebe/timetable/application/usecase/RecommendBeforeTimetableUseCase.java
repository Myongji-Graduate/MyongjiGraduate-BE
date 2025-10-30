package com.plzgraduate.myongjigraduatebe.timetable.application.usecase;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendBeforeTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.recommend.FreeDay;

import java.util.List;

public interface RecommendBeforeTimetableUseCase {
    RecommendBeforeTimetableResponse recommend(Long userId, int targetCredits, List<FreeDay> freeDays, int year, int semester);
}
