package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;

import java.util.List;

public interface TimetableQueryRepository {
    List<TimetableJpaEntity> searchByCondition(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest condition);
    
    List<TimetableJpaEntity> searchByConditionWithPagination(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest condition, int offset, int limit);
    
    long countByCondition(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest condition);
}
