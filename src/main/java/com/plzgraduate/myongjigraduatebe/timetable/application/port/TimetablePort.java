package com.plzgraduate.myongjigraduatebe.timetable.application.port;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;

import java.util.List;

public interface TimetablePort {
    List<Timetable> findByYearAndSemester(int year, int semester);
    List<Timetable> findByKeyword(int year, int semester, String keyword);
    List<Timetable> searchByCondition(int year, int semester, TimetableSearchConditionRequest condition);

    /** 학기 전체 기준 강의코드만 조회 */
//    List<String> findLectureCodesByYearAndSemester(int year, int semester);

    /** 주어진 강의코드 집합에 해당하는 시간표 엔티티 조회 */
 //   List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, List<String> codes);

    /** 주어진 강의코드 집합을 제외한 시간표 엔티티 조회 */
  //  List<Timetable> findByYearSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes);

//    List<Timetable> searchCombined(
//            Long userId,
//            int year,
//            int semester,
//            TakenFilter filter,
//            TimetableSearchConditionRequest condition,
//            boolean restrictToMajorAndCommons,
//            String userMajor // null 가능
//    );

    // NOT_TAKEN/TAKEN 필터링용
    List<String> findLectureCodesByYearAndSemester(int year, int semester);
    List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, List<String> codes);
    List<Timetable> findByYearSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes);
}
