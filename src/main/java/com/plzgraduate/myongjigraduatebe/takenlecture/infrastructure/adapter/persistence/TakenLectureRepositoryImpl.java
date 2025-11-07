package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecturedetail.infrastructure.adapter.persistence.entity.QLectureReviewJpaEntity;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


import java.util.*;
import java.util.stream.Collectors;


@Repository
@RequiredArgsConstructor
public class TakenLectureRepositoryImpl
        implements TakenLectureRepositoryCustom, PopularLecturePort {

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
        }).collect(Collectors.toUnmodifiableList());

        return categoryResolver.attachWithoutContext(rawResult);
    }

    @Override
    public List<PopularLectureDto> getPopularLecturesSlice(int limit, String cursor) {
        List<PopularLectureDto> all = getPopularLecturesByTotalCount();

        int startIndex = 0;
        if (cursor != null) {
            for (int i = 0; i < all.size(); i++) {
                if (all.get(i).getLectureId().equals(cursor)) {
                    startIndex = i + 1;
                    break;
                }
            }
        }
        int endIndex = Math.min(startIndex + limit + 1, all.size());
        return all.subList(startIndex, endIndex);
    }

    @Override
    public List<PopularLectureDto> getLecturesByCategory(
            String major, int entryYear, PopularLectureCategory category, int limit, String cursor) {

        // 1. 전체 인기 강의 조회 (getSections()와 동일한 방식)
        List<PopularLectureDto> allLectures = getPopularLecturesByTotalCount();
        List<PopularLectureDto> withCategory = categoryResolver.attachWithContext(allLectures, major, entryYear);

        // 2. 해당 카테고리만 필터링
        List<PopularLectureDto> filtered = withCategory.stream()
                .filter(dto -> dto.getCategoryName() == category)
                .collect(Collectors.toList());

        // 3. cursor 기준으로 시작 인덱스 찾기
        int startIndex = 0;
        if (cursor != null) {
            for (int i = 0; i < filtered.size(); i++) {
                if (filtered.get(i).getLectureId().equals(cursor)) {
                    startIndex = i + 1;  // cursor 다음부터 시작
                    break;
                }
            }
        }

        // 4. limit+1개 반환 (hasMore 판단용)
        int endIndex = Math.min(startIndex + limit + 1, filtered.size());
        return filtered.subList(startIndex, endIndex);
    }

    @Override
    public List<PopularLecturesInitResponse.SectionMeta> getSections(String major, int entryYear) {
        List<PopularLectureDto> allLectures = getPopularLecturesByTotalCount();
        List<PopularLectureDto> withCategory = categoryResolver.attachWithContext(allLectures, major, entryYear);

        Map<PopularLectureCategory, Long> groupedByCategory = withCategory.stream()
                .collect(Collectors.groupingBy(PopularLectureDto::getCategoryName, Collectors.counting()));

        return groupedByCategory.entrySet().stream()
                .map(entry -> PopularLecturesInitResponse.SectionMeta.builder()
                        .categoryName(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
