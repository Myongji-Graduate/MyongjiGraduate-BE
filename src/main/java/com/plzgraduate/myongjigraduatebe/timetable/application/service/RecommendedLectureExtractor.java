package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
class RecommendedLectureExtractor {

    private final CalculateSingleDetailGraduationUseCase calc;

    /**
     * 단일 카테고리의 haveToLectures에 포함된 과목 코드(ID)만 추출
     * DetailGraduationResult.detailCategory[*].haveToLectures[*].id 를 전부 모아 반환한다.
     */
    public List<String> extractRecommendedLectureIds(Long userId, GraduationCategory category) {
        DetailGraduationResult result = calc.calculateSingleDetailGraduation(userId, category);
        if (result == null || result.getDetailCategory() == null) {
            return java.util.List.of();
        }
        return result.getDetailCategory().stream()
                .filter(java.util.Objects::nonNull)
                .flatMap(dc -> {
                    var lectures = dc.getHaveToLectures();
                    if (lectures == null) return java.util.stream.Stream.empty();
                    return lectures.stream()
                            .filter(java.util.Objects::nonNull)
                            .map(l -> l.getId());
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
}