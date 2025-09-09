package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;

import java.util.List;

public interface TimetableQueryRepository {
    List<TimetableJpaEntity> searchByCondition(int year, int semester,TimetableSearchConditionRequest condition);
    List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeIn(int year, int semester, List<String> codes);

    List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes);
}
