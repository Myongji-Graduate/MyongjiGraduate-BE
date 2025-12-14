package com.plzgraduate.myongjigraduatebe.timetable.application.port;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;

import java.util.List;

public interface TimetablePort {
    List<Timetable> findByYearAndSemester(int year, int semester);
    List<Timetable> searchByCondition(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest condition);


    // NOT_TAKEN/TAKEN 필터링용
    List<String> findLectureCodesByYearAndSemester(int year, int semester);
    List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, CampusFilter campus, List<String> codes);
    List<Timetable> findByYearSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes);
}
