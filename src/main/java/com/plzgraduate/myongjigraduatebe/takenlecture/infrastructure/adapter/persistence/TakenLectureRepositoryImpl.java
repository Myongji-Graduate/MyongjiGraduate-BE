package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.QLectureReviewJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QCoreCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QCommonCultureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QBasicAcademicalCultureLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QMajorLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.user.domain.model.College;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class TakenLectureRepositoryImpl implements PopularLecturePort {

    private static final QTakenLectureJpaEntity takenLecture = QTakenLectureJpaEntity.takenLectureJpaEntity;

    private final JPAQueryFactory jpaQueryFactory;
    private final LectureCategoryResolver categoryResolver;

    @Override
    public List<PopularLectureDto> getPopularLecturesByTotalCount() {
        QLectureReviewJpaEntity lectureReviewJpaEntity = QLectureReviewJpaEntity.lectureReviewJpaEntity;
        NumberExpression<Long> countExp = takenLecture.id.count();
        JPQLQuery<Double> avgExpr = JPAExpressions
                .select(lectureReviewJpaEntity.rating.avg())
                .from(lectureReviewJpaEntity)
                .where(lectureReviewJpaEntity.subject.eq(takenLecture.lecture.name));

        List<Tuple> rows = jpaQueryFactory
                .select(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit,
                        countExp,
                        avgExpr
                )
                .from(takenLecture)
                .groupBy(takenLecture.lecture.id, takenLecture.lecture.name, takenLecture.lecture.credit)
                .orderBy(countExp.desc(), takenLecture.lecture.id.desc())
                .fetch();

        List<PopularLectureDto> rawResult = rows.stream().map(tuple -> {
            String id = tuple.get(takenLecture.lecture.id);
            String name = tuple.get(takenLecture.lecture.name);
            Integer credit = tuple.get(takenLecture.lecture.credit);
            Long total = tuple.get(countExp);
            Double avg = tuple.get(avgExpr);

            int creditVal = (credit == null) ? 0 : credit;
            long totalVal = (total == null) ? 0L : total;
            double avgVal = (avg == null) ? 0.0 : avg;

            return PopularLectureDto.ofWithAverage(id, name, creditVal, totalVal, null, avgVal);
        }).toList();

        return categoryResolver.attachWithoutContext(rawResult);
    }



    @Override
    public List<PopularLectureDto> getLecturesByCategory(
            String major, int entryYear, PopularLectureCategory category, int limit, String cursor) {
        List<Tuple> rows = fetchPopularByCategoryWithKeyset(category, major, entryYear, limit, cursor);
        // Use DISTINCT to avoid inflated counts due to joins (e.g., reviews or major mappings)
        NumberExpression<Long> countExp = takenLecture.id.countDistinct();
        return rows.stream().map(t -> toDto(t, countExp, category)).toList();
    }


    private record Cursor(long total, String lectureId) {
    }

    private Cursor parseCursor(String raw) {
        if (raw == null || raw.isBlank()) return null;
        int idx = raw.indexOf(':');
        if (idx < 0) {
            // id-only cursor (new format)
            return new Cursor(-1L, raw);
        }
        try {
            long total = Long.parseLong(raw.substring(0, idx));
            String id = raw.substring(idx + 1);
            return new Cursor(total, id);
        } catch (NumberFormatException e) {
            // Fallback to id-only if prefix isn't a number
            return new Cursor(-1L, raw.substring(idx + 1));
        }
    }

    private List<Tuple> fetchPopularByCategoryWithKeyset(
            PopularLectureCategory category, String major, int entryYear, int limit, String rawCursor
    ) {
        QLectureReviewJpaEntity review = QLectureReviewJpaEntity.lectureReviewJpaEntity;
        // Avoid row-multiplication inflating totals when joining review or major/category maps
        NumberExpression<Long> countExp = takenLecture.id.countDistinct();
        Expression<Double> avgExpr = review.rating.avg();

        Cursor c = parseCursor(rawCursor);
        BooleanExpression keysetCond = null;
        if (c != null) {
            if (c.total >= 0) {
                // legacy composite cursor: total:id
                keysetCond = countExp.lt(c.total)
                        .or(countExp.eq(c.total).and(takenLecture.lecture.id.lt(c.lectureId)));
            } else {
                // id-only cursor: use secondary order key
                keysetCond = takenLecture.lecture.id.lt(c.lectureId);
            }
        }

        JPAQuery<Tuple> query = jpaQueryFactory
                .select(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit,
                        countExp,
                        avgExpr
                )
                .from(takenLecture)
                .leftJoin(review).on(review.subject.eq(takenLecture.lecture.name));

        applyCategoryJoin(category, major, entryYear, query);

        query.groupBy(takenLecture.lecture.id, takenLecture.lecture.name, takenLecture.lecture.credit);
        if (keysetCond != null) {
            query.having(keysetCond);
        }

        return query
                .orderBy(countExp.desc(), takenLecture.lecture.id.desc())
                .limit(limit + 1L)
                .fetch();
    }

    private void applyCategoryJoin(PopularLectureCategory category, String major, int entryYear, JPAQuery<Tuple> query) {
        QCoreCultureJpaEntity core = QCoreCultureJpaEntity.coreCultureJpaEntity;
        QCommonCultureJpaEntity common = QCommonCultureJpaEntity.commonCultureJpaEntity;
        QBasicAcademicalCultureLectureJpaEntity bac = QBasicAcademicalCultureLectureJpaEntity.basicAcademicalCultureLectureJpaEntity;
        QMajorLectureJpaEntity majorMap = QMajorLectureJpaEntity.majorLectureJpaEntity;

        switch (category) {
            case CORE_CULTURE -> query.join(core).on(
                    core.lectureJpaEntity.id.eq(takenLecture.lecture.id)
                            .and(core.startEntryYear.loe(entryYear))
                            .and(core.endEntryYear.goe(entryYear))
            );
            case COMMON_CULTURE -> query.join(common).on(
                    common.lectureJpaEntity.id.eq(takenLecture.lecture.id)
                            .and(common.startEntryYear.loe(entryYear))
                            .and(common.endEntryYear.goe(entryYear))
            );
            case BASIC_ACADEMICAL_CULTURE -> {
                String college = College.findBelongingCollege(major, entryYear).getName();
                query.join(bac).on(
                        bac.lectureJpaEntity.id.eq(takenLecture.lecture.id)
                                .and(bac.college.eq(college))
                );
            }
            case MANDATORY_MAJOR, ELECTIVE_MAJOR -> {
                int mandatory = (category == PopularLectureCategory.MANDATORY_MAJOR) ? 1 : 0;
                query.join(majorMap).on(
                        majorMap.lectureJpaEntity.id.eq(takenLecture.lecture.id)
                                .and(majorMap.major.in(java.util.List.of(major)))
                                .and(majorMap.mandatory.eq(mandatory))
                                .and(majorMap.startEntryYear.loe(entryYear))
                                .and(majorMap.endEntryYear.goe(entryYear))
                );
            }
            default -> throw new IllegalArgumentException("Unsupported category: " + category);
        }
    }

    private PopularLectureDto toDto(Tuple tuple, NumberExpression<Long> countExp, PopularLectureCategory category) {
        String id = tuple.get(takenLecture.lecture.id);
        String name = tuple.get(takenLecture.lecture.name);
        Integer credit = tuple.get(takenLecture.lecture.credit);
        Long total = tuple.get(countExp);
        Double avg = tuple.get(4, Double.class);

        int creditVal = (credit == null) ? 0 : credit;
        long totalVal = (total == null) ? 0L : total;
        double avgVal = (avg == null) ? 0.0 : avg;

        return PopularLectureDto.ofWithAverage(id, name, creditVal, totalVal, category, avgVal);
    }

    @Override
    public List<PopularLecturesInitResponse.SectionMeta> getSections(String major, int entryYear) {
        List<PopularLectureDto> allLectures = getPopularLecturesByTotalCount();
        List<PopularLectureDto> withCategory = categoryResolver.attachWithContext(allLectures, major, entryYear);

        Map<PopularLectureCategory, Long> groupedByCategory = withCategory.stream()
                .filter(dto -> dto.getCategoryName() != null)
                .collect(Collectors.groupingBy(PopularLectureDto::getCategoryName, Collectors.counting()));

        // Deterministic order: BASIC -> CORE -> COMMON -> MANDATORY -> ELECTIVE
        return Arrays.stream(PopularLectureCategory.values())
                .filter(c -> c != PopularLectureCategory.ALL)
                .map(c -> PopularLecturesInitResponse.SectionMeta.builder()
                        .categoryName(c)
                        .total(groupedByCategory.getOrDefault(c, 0L))
                        .build())
                .filter(meta -> meta.getTotal() > 0)
                .toList();
    }
}
