package com.plzgraduate.myongjigraduatebe.timetable.application.usecase;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;

import java.util.List;

public interface UserTimetableUseCase {
    void replaceLectures(Long userId, int year, int semester, List<Long> timetableIds);
    List<TimetableResponse> getMyLectures(Long userId, int year, int semester);
    void clearLectures(Long userId, int year, int semester);
}
