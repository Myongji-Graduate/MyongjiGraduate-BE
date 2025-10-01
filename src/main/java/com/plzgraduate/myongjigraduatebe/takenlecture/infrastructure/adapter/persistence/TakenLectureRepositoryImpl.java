package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.QFindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
import com.querydsl.jpa.impl.JPAQuery;
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
        List<PopularLectureDto> rawResult = jpaQueryFactory
                .select(new QFindPopularLectureDto(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit,
                        takenLecture.id.count()
                ))
                .from(takenLecture)
                .groupBy(takenLecture.lecture.id, takenLecture.lecture.name, takenLecture.lecture.credit)
                .orderBy(takenLecture.id.count().desc(), takenLecture.lecture.id.desc())
                .fetch();

        return categoryResolver.attachWithoutContext(rawResult);
    }

    @Override
    public List<PopularLectureDto> getLecturesByCategory(
            String major, int entryYear, PopularLectureCategory category, int limit, String cursor) {

        JPAQuery<PopularLectureDto> baseQuery = jpaQueryFactory
                .select(new QFindPopularLectureDto(
                        takenLecture.lecture.id,
                        takenLecture.lecture.name,
                        takenLecture.lecture.credit,
                        takenLecture.id.count()
                ))
                .from(takenLecture)
                .groupBy(takenLecture.lecture.id, takenLecture.lecture.name, takenLecture.lecture.credit)
                .orderBy(takenLecture.id.count().desc(), takenLecture.lecture.id.desc())
                .limit(limit + 1);

        if (cursor != null) {
            baseQuery.where(takenLecture.lecture.id.gt(cursor));
        }

        List<PopularLectureDto> rawResult = baseQuery.fetch();

        return categoryResolver.attachWithContext(rawResult, major, entryYear).stream()
                .filter(dto -> dto.getCategoryName() == category)
                .collect(Collectors.toUnmodifiableList());
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
