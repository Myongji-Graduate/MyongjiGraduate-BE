package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.QTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TimetableQueryDslRepository implements TimetableQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<TimetableJpaEntity> searchByCondition(int year, int semester,TimetableSearchConditionRequest condition) {
          QTimetableJpaEntity timetable = QTimetableJpaEntity.timetableJpaEntity;

        return queryFactory.selectFrom(timetable)
                .where(
                        timetable.year.eq(year),
                        timetable.semester.eq(semester),
                        condition.getKeyword() != null ?
                                timetable.name.containsIgnoreCase(condition.getKeyword())
                                        .or(timetable.lectureCode.containsIgnoreCase(condition.getKeyword())) : null,
                        condition.getDepartment() != null ? timetable.department.eq(condition.getDepartment()) : null,
                        condition.getProfessor() != null ? timetable.professor.eq(condition.getProfessor()) : null,
                        condition.getCampus() != null ? timetable.campus.eq(condition.getCampus()) : null
                )
                .fetch();
    }
}
