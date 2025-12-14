package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FindTimeTableService implements FindTimetableUseCase {

    private final TimetablePort timetablePort;
    private final FindTakenLecturePort findTakenLecturePort;
    private final RecommendedLectureExtractor recommendedExtractor;
    private final FindUserUseCase findUserUseCase;

    @Override
    public List<Timetable> findByYearAndSemester(int year, int semester) {
        return timetablePort.findByYearAndSemester(year, semester);
    }

    @Override
    public List<Timetable> searchCombined(
            Long userId,
            int year,
            int semester,
            CampusFilter campus,
            TakenFilter filter,
            TimetableSearchConditionRequest condition,
            GraduationCategory recommendedCategory
    ) {
        List<Timetable> base = (condition == null)
                ? timetablePort.findByYearAndSemester(year, semester)
                : timetablePort.searchByCondition(year, semester, campus, condition);
        if (base.isEmpty()) return List.of();
        if (filter == TakenFilter.ALL && recommendedCategory == null) return base;

        List<String> baseCodes = extractBaseCodes(base);
        List<GraduationCategory> sourceCategories = (recommendedCategory != null)
                ? List.of(recommendedCategory)
                : determineCategories(userId);
        RecommendedLectureExtractor.ExtractMode mode = resolveExtractMode(filter);
        Set<String> recommendedIds = extractRecommendedLectureCodes(userId, sourceCategories, mode);
        if (recommendedIds.isEmpty()) return List.of();

        List<String> candidate = filterOpenSubjects(recommendedIds, baseCodes);
        List<String> finalCodes = filterAlreadyTakenIfNecessary(candidate, userId, mode);
        if (finalCodes.isEmpty()) return List.of();

        return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, finalCodes);
    }

    private RecommendedLectureExtractor.ExtractMode resolveExtractMode(TakenFilter filter) {
        return switch (filter) {
            case TAKEN -> RecommendedLectureExtractor.ExtractMode.TAKEN;
            case NOT_TAKEN -> RecommendedLectureExtractor.ExtractMode.HAVE_TO;
            default -> RecommendedLectureExtractor.ExtractMode.BOTH;
        };
    }

    private Set<String> extractRecommendedLectureCodes(Long userId, List<GraduationCategory> categories, RecommendedLectureExtractor.ExtractMode mode) {
        Set<String> result = new HashSet<>();
        for (GraduationCategory cat : categories) {
            List<String> ids = recommendedExtractor.extractLectureIds(userId, cat, mode);
            if (ids != null && !ids.isEmpty()) {
                result.addAll(ids);
            }
        }
        return result;
    }

    private List<String> extractBaseCodes(List<Timetable> base) {
        return base.stream()
            .map(Timetable::getLectureCode)
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());
    }

    private List<String> filterOpenSubjects(Set<String> recommendedIds, List<String> baseCodes) {
        Set<String> openNow = new HashSet<>(baseCodes);
        return recommendedIds.stream()
            .filter(openNow::contains)
            .collect(Collectors.toList());
    }

    private List<String> filterAlreadyTakenIfNecessary(List<String> candidate, Long userId, RecommendedLectureExtractor.ExtractMode mode) {
        if (mode != RecommendedLectureExtractor.ExtractMode.HAVE_TO) return candidate;
        List<String> alreadyTaken = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, candidate);
        Set<String> takenSet = new HashSet<>(alreadyTaken);
        return candidate.stream()
            .filter(c -> !takenSet.contains(c))
            .collect(Collectors.toList());
    }

    private List<GraduationCategory> determineCategories(Long userId) {
        List<GraduationCategory> dynamic = new ArrayList<>(List.of(
            GraduationCategory.PRIMARY_MANDATORY_MAJOR,
            GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
            GraduationCategory.COMMON_CULTURE,
            GraduationCategory.CORE_CULTURE,
            GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE
        ));
        User user = findUserUseCase.findUserById(userId);
        switch (user.getStudentCategory()) {
            case SUB_MAJOR:
                dynamic.add(GraduationCategory.SUB_MAJOR);
                break;
            case DUAL_MAJOR:
                dynamic.add(GraduationCategory.DUAL_ELECTIVE_MAJOR);
                dynamic.add(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE);
                break;
            default:
                break; // NORMAL
        }
        return dynamic;
    }

    @Override
    public FindTimetableUseCase.SearchCombinedResult searchCombinedWithPagination(
            Long userId,
            FindTimetableUseCase.SearchParams searchParams,
            FindTimetableUseCase.PaginationParams pagination
    ) {
        // 기존 searchCombined 로직을 재사용하여 전체 결과 조회
        List<Timetable> allResults = searchCombined(
                userId,
                searchParams.year(),
                searchParams.semester(),
                searchParams.campus(),
                searchParams.filter(),
                searchParams.condition(),
                searchParams.recommendedCategory()
        );
        
        long totalCount = allResults.size();
        
        // 페이지네이션 적용 (1-based page)
        int offset = (pagination.page() - 1) * pagination.limit();
        int endIndex = Math.min(offset + pagination.limit(), allResults.size());
        
        List<Timetable> paginatedResults = (offset >= allResults.size())
                ? List.of()
                : allResults.subList(offset, endIndex);
        
        return new FindTimetableUseCase.SearchCombinedResult(paginatedResults, totalCount);
    }
}