package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindTimeTableService implements FindTimetableUseCase {

    private final TimetablePort timetablePort;

    @Override
    public List<Timetable> findByYearAndSemester(int year, int semester) {
        return timetablePort.findByYearAndSemester(year, semester);
    }

    @Override
    public List<Timetable> findByKeyword(int year, int semester, String keyword) {
        return timetablePort.findByKeyword(year, semester, keyword);
    }

    @Override
    public List<Timetable> searchByCondition(int year, int semester,TimetableSearchConditionRequest condition) {
        return timetablePort.searchByCondition(year, semester, condition);
    }

}
