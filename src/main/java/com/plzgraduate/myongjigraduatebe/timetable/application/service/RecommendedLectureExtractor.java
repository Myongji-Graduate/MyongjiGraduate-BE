package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.graduation.application.usecase.CalculateSingleDetailGraduationUseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;
import lombok.Getter;
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

    /**
     * detailCategory 단위로 HAVE_TO 강의와 남은 학점 정보를 반환.
     */
    public List<DetailRecommendation> extractDetailRecommendations(Long userId, GraduationCategory category) {
        try {
            DetailGraduationResult result = calc.calculateSingleDetailGraduation(userId, category);
            if (result == null) {
                return List.of();
            }

            List<DetailCategoryResult> detailCategories = result.getDetailCategory();
            if (detailCategories == null || detailCategories.isEmpty()) {
                int remaining = Math.max(0, result.getTotalCredit() - (int) result.getTakenCredit());
                List<String> haveTo = extractRecommendedLectureIds(userId, category);
                if (remaining <= 0 && haveTo.isEmpty()) {
                    return List.of();
                }
                return List.of(DetailRecommendation.of(category, category.getName(), remaining, haveTo));
            }

            return detailCategories.stream()
                    .filter(Objects::nonNull)
                    .map(detail -> {
                        String detailName = detail.getDetailCategoryName();
                        int remaining = Math.max(0, detail.getTotalCredits() - detail.getTakenCredits());
                        List<String> haveTo = toLectureIds(detail.getHaveToLectures());
                        if (remaining <= 0 && haveTo.isEmpty()) {
                            return null;
                        }
                        return DetailRecommendation.of(
                                category,
                                detailName != null ? detailName : category.getName(),
                                remaining,
                                haveTo
                        );
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("UNFITTED_GRADUATION_CATEGORY")) {
                return List.of();
            }
            throw e;
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("No calculate detail graduation case found")) {
                return List.of();
            }
            throw e;
        }
    }

    private List<String> toLectureIds(List<Lecture> lectures) {
        if (lectures == null || lectures.isEmpty()) {
            return List.of();
        }
        return lectures.stream()
                .filter(Objects::nonNull)
                .map(Lecture::getId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Getter
    static final class DetailRecommendation {
        private final GraduationCategory graduationCategory;
        private final String detailCategoryName;
        private final int remainingCredit;
        private final List<String> haveToLectureIds;

        private DetailRecommendation(GraduationCategory graduationCategory, String detailCategoryName,
                                     int remainingCredit, List<String> haveToLectureIds) {
            this.graduationCategory = graduationCategory;
            this.detailCategoryName = detailCategoryName;
            this.remainingCredit = remainingCredit;
            this.haveToLectureIds = haveToLectureIds == null ? List.of() : List.copyOf(haveToLectureIds);
        }

        static DetailRecommendation of(GraduationCategory graduationCategory, String detailCategoryName,
                                       int remainingCredit, List<String> haveToLectureIds) {
            return new DetailRecommendation(graduationCategory, detailCategoryName, remainingCredit, haveToLectureIds);
        }
    }
}
