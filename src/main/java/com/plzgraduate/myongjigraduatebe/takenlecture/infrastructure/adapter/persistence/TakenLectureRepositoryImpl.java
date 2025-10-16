package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.QPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.LectureCategoryResolver;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.QTakenLectureJpaEntity;
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
                .select(new QPopularLectureDto(
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
