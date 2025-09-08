package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.QTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TimetableQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    public List<TimetableJpaEntity> searchCombined(
            int year,
            int semester,
            TimetableSearchConditionRequest condition,
            TakenFilter filter,
            Long userId,
            boolean restrictToMajorAndCommons,
            String userMajor
    ) {
        QTimetableJpaEntity tt = QTimetableJpaEntity.timetableJpaEntity;
        QTakenLectureJpaEntity tk = QTakenLectureJpaEntity.takenLectureJpaEntity;
        QLectureJpaEntity lec = QLectureJpaEntity.lectureJpaEntity;

        // --- 기본 조건 ---
        BooleanBuilder where = new BooleanBuilder()
                .and(tt.year.eq(year))
                .and(tt.semester.eq(semester));

        if (condition != null) {
            if (condition.getKeyword() != null && !condition.getKeyword().isBlank()) {
                where.and(
                        tt.name.containsIgnoreCase(condition.getKeyword())
                                .or(tt.lectureCode.containsIgnoreCase(condition.getKeyword()))
                );
            }
            if (condition.getDepartment() != null) {
                where.and(tt.department.eq(condition.getDepartment()));
            }
            if (condition.getProfessor() != null) {
                where.and(tt.professor.eq(condition.getProfessor()));
            }
            if (condition.getCampus() != null) {
                where.and(tt.campus.eq(condition.getCampus()));
            }
        }

        // --- 이수 상태 필터 (exists / not exists) ---
        if (filter == TakenFilter.TAKEN) {
            // 이수 강의만
            where.and(JPAExpressions
                    .selectOne()
                    .from(tk)
                    .join(tk.lecture, lec)
                    .where(
                            tk.user.id.eq(userId),
                            lec.id.eq(tt.lectureCode) // 문자열 매칭
                    )
                    .exists());
        } else if (filter == TakenFilter.NOT_TAKEN) {
            // 미이수 강의만
            where.and(JPAExpressions
                    .selectOne()
                    .from(tk)
                    .join(tk.lecture, lec)
                    .where(
                            tk.user.id.eq(userId),
                            lec.id.eq(tt.lectureCode)
                    )
                    .notExists());
        }

        // --- 미이수에서 “자연교양/인문교양/사용자전공만” 제한 ---
        if (restrictToMajorAndCommons) {
            where.and(
                    tt.department.eq("자연교양")
                            .or(tt.department.eq("인문교양"))
                            .or(userMajor != null ? tt.department.eq(userMajor) : Expressions.FALSE)
            );
        }

        return queryFactory.selectFrom(tt).where(where).fetch();
    }
}