package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.GetPopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
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
        implements TakenLectureRepositoryCustom, GetPopularLecturePort {

    private static final QTakenLectureJpaEntity takenLecture = QTakenLectureJpaEntity.takenLectureJpaEntity;

    private final JPAQueryFactory jpaQueryFactory;
    private final LectureCategoryResolver categoryResolver;

    @Override
    public List<FindPopularLectureDto> getPopularLecturesByTotalCount() {
        List<FindPopularLectureDto> rawResult = jpaQueryFactory
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
    public List<FindPopularLectureDto> getLecturesByCategory(
            String major, int entryYear, PopularLectureCategory category, int limit, String cursor) {

        JPAQuery<FindPopularLectureDto> baseQuery = jpaQueryFactory
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

        List<FindPopularLectureDto> rawResult = baseQuery.fetch();

        return categoryResolver.attachWithContext(rawResult, major, entryYear).stream()
                .filter(dto -> dto.getCategoryName() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<PopularLecturesInitResponse.SectionMeta> getSections(String major, int entryYear) {
        List<FindPopularLectureDto> allLectures = getPopularLecturesByTotalCount();
        List<FindPopularLectureDto> withCategory = categoryResolver.attachWithContext(allLectures, major, entryYear);

        Map<PopularLectureCategory, Long> groupedByCategory = withCategory.stream()
                .collect(Collectors.groupingBy(FindPopularLectureDto::getCategoryName, Collectors.counting()));

        return groupedByCategory.entrySet().stream()
                .map(entry -> PopularLecturesInitResponse.SectionMeta.builder()
                        .categoryName(entry.getKey())
                        .total(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }
}
