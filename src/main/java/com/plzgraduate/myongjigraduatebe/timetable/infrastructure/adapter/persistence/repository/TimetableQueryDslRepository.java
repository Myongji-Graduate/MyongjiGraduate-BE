package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.QTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TimetableQueryDslRepository implements TimetableQueryRepository {

    private final JPAQueryFactory query;

    private static final QTimetableJpaEntity timetable = QTimetableJpaEntity.timetableJpaEntity;

    @Override
    public List<TimetableJpaEntity> searchByCondition(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest c) {
        BooleanBuilder where = new BooleanBuilder()
                .and(timetable.year.eq(year))
                .and(timetable.semester.eq(semester));


        if (campus != null) {
            where.and(timetable.campus.eq(campus.name()));
        }


        if (c != null) {
            if (hasText(c.getProfessor())) {
                where.and(timetable.professor.lower()
                        .contains(c.getProfessor().trim().toLowerCase()));
            }

            if (hasText(c.getKeyword())) {
                String kw = c.getKeyword().trim().toLowerCase();
                where.and(timetable.name.lower().contains(kw));
            }
        }

        // 정렬: 요일 -> 시작분 -> 분반(필요시)
        return query.selectFrom(timetable)
                .where(where)
                .orderBy(
                        timetable.day1.asc().nullsLast(),
                        timetable.startMinute1.asc().nullsLast(),
                        timetable.classDivision.asc()
                )
                .fetch();
    }

    @Override
    public List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeIn(int year, int semester, List<String> codes) {
        if (codes == null || codes.isEmpty()) return List.of();

        return query.selectFrom(timetable)
                .where(timetable.year.eq(year)
                        .and(timetable.semester.eq(semester))
                        .and(timetable.lectureCode.in(codes)))
                .fetch();
    }

    @Override
    public List<TimetableJpaEntity> findByYearAndSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes) {
        if (codes == null || codes.isEmpty()) {
            // not in (empty) 은 모든 행이 통과하므로, 빈 경우엔 그냥 해당 학기 전체 반환
            return query.selectFrom(timetable)
                    .where(timetable.year.eq(year)
                            .and(timetable.semester.eq(semester)))
                    .fetch();
        }

        return query.selectFrom(timetable)
                .where(timetable.year.eq(year)
                        .and(timetable.semester.eq(semester))
                        .and(timetable.lectureCode.notIn(codes)))
                .fetch();
    }

    private boolean hasText(String s) {
        return s != null && !s.trim().isEmpty();
    }
}