package com.plzgraduate.myongjigraduatebe.timetable.application.usecase;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;

import java.util.List;

public interface FindTimetableUseCase {
    List<Timetable> findByYearAndSemester(int year, int semester);
    List<Timetable> findByKeyword(int year, int semester, String keyword);

//    List<Timetable> searchByCondition(int year, int semester,TimetableSearchConditionRequest condition);

    List<Timetable> searchCombined(
            Long userId,
            int year,
            int semester,
            TakenFilter filter,
            TimetableSearchConditionRequest condition,
            boolean restrictToMajorAndCommons
    );

}
