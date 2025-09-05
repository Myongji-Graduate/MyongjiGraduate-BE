package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TimetableRepository extends JpaRepository<TimetableJpaEntity, Long> {
    List<TimetableJpaEntity> findAllByYearAndSemester(int year, int semester);
    List<TimetableJpaEntity> findByYearAndSemesterAndNameContaining(int year, int semester, String keyword);
}