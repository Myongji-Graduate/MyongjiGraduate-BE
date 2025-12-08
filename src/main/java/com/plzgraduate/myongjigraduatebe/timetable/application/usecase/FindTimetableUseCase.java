package com.plzgraduate.myongjigraduatebe.timetable.application.usecase;

import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;

import java.util.List;

public interface FindTimetableUseCase {
    List<Timetable> findByYearAndSemester(int year, int semester);
    List<Timetable> findByKeyword(int year, int semester, String keyword);

    List<Timetable> searchCombined(
            Long userId,
            int year,
            int semester,
            CampusFilter campus,
            TakenFilter filter,
            TimetableSearchConditionRequest condition,
            GraduationCategory recommendedCategory // NOT_TAKEN일 때 필수
    );

    SearchCombinedResult searchCombinedWithPagination(
            Long userId,
            int year,
            int semester,
            CampusFilter campus,
            TakenFilter filter,
            TimetableSearchConditionRequest condition,
            GraduationCategory recommendedCategory,
            int page,
            int limit
    );

    class SearchCombinedResult {
        private final List<Timetable> data;
        private final long totalCount;

        public SearchCombinedResult(List<Timetable> data, long totalCount) {
            this.data = data;
            this.totalCount = totalCount;
        }

        public List<Timetable> getData() {
            return data;
        }

        public long getTotalCount() {
            return totalCount;
        }
    }
}
