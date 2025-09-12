package com.plzgraduate.myongjigraduatebe.timetable.application.port;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;

import java.util.List;

public interface UserTimetablePort {
    void deleteByUserAndSemester(Long userId, int year, int semester);
    void saveAll(List<UserTimetable> userTimetables);
    List<Timetable> findTimetablesByUserAndSemester(Long userId, int year, int semester);
}
