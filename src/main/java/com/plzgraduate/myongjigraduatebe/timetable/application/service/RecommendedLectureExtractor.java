package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
class RecommendedLectureExtractor {

    enum ExtractMode { HAVE_TO, TAKEN, BOTH }

    private final CalculateSingleDetailGraduationUseCase calc;

    /**
     * 카테고리별 DetailGraduationResult에서 강의 ID 추출
     * - HAVE_TO  : haveToLectures (미이수 추천)
     * - TAKEN    : takenLectures  (이미 이수)
     * - BOTH     : 둘 다
     */
    public List<String> extractLectureIds(Long userId, GraduationCategory category, ExtractMode mode) {
        DetailGraduationResult result = calc.calculateSingleDetailGraduation(userId, category);
        if (result == null || result.getDetailCategory() == null) {
            return List.of();
        }

        return result.getDetailCategory().stream()
                .filter(Objects::nonNull)
                .flatMap(dc -> {
                    Stream<Lecture> s1 = Stream.empty();
                    Stream<Lecture> s2 = Stream.empty();

                    if (mode == ExtractMode.HAVE_TO || mode == ExtractMode.BOTH) {
                        var haveTo = dc.getHaveToLectures();
                        if (haveTo != null) s1 = haveTo.stream();
                    }
                    if (mode == ExtractMode.TAKEN || mode == ExtractMode.BOTH) {
                        var taken = dc.getTakenLectures();
                        if (taken != null) s2 = taken.stream();
                    }
                    return Stream.concat(s1, s2);
                })
                .filter(Objects::nonNull)
                .map(Lecture::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /** 하위호환: 기존 호출부는 미이수(HAVE_TO)로 동작 */
    public List<String> extractRecommendedLectureIds(Long userId, GraduationCategory category) {
        return extractLectureIds(userId, category, ExtractMode.HAVE_TO);
    }
}