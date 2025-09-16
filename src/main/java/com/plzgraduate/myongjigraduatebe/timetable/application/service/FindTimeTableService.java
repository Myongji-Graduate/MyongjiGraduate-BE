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
    public List<Timetable> findByKeyword(int year, int semester, String keyword) {
        return timetablePort.findByKeyword(year, semester, keyword);
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
        User user = findUserUseCase.findUserById(userId);

        List<Timetable> base = (condition == null)
                ? timetablePort.findByYearAndSemester(year, semester)
                : timetablePort.searchByCondition(year, semester, campus, condition);

        if (base.isEmpty()) return List.of();

        if (filter == TakenFilter.ALL && recommendedCategory == null) {
            return base;
        }

        // 현재 학기 개설 코드 목록
        List<String> baseCodes = base.stream()
                .map(Timetable::getLectureCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        // TAKEN / NOT_TAKEN / (ALL+category) 공통
        List<GraduationCategory> sourceCategories;
        if (recommendedCategory != null) {
            sourceCategories = List.of(recommendedCategory);
        } else {
            List<GraduationCategory> dynamic = new ArrayList<>(List.of(
                    GraduationCategory.PRIMARY_MANDATORY_MAJOR,
                    GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
                    GraduationCategory.COMMON_CULTURE,
                    GraduationCategory.CORE_CULTURE,
                    GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE
            ));
            switch (user.getStudentCategory()) {
                case SUB_MAJOR:
                    dynamic.add(GraduationCategory.SUB_MAJOR);
                    break;
                case DUAL_MAJOR:
                    dynamic.add(GraduationCategory.DUAL_ELECTIVE_MAJOR);
                    dynamic.add(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE);
                    break;
                default:
                    // NORMAL
                    break;
            }
            sourceCategories = dynamic;
        }

        RecommendedLectureExtractor.ExtractMode mode;
        if (filter == TakenFilter.TAKEN) {
            mode = RecommendedLectureExtractor.ExtractMode.TAKEN;
        } else if (filter == TakenFilter.NOT_TAKEN) {
            mode = RecommendedLectureExtractor.ExtractMode.HAVE_TO;
        } else {
            mode = RecommendedLectureExtractor.ExtractMode.BOTH;
        }

        // 카테고리별 ID 수집 + 중복제거
        Set<String> recommendedIds = new HashSet<>();
        for (GraduationCategory cat : sourceCategories) {
            List<String> ids = recommendedExtractor.extractLectureIds(userId, cat, mode);
            if (ids != null && !ids.isEmpty()) {
                recommendedIds.addAll(ids);
            }
        }
        if (recommendedIds.isEmpty()) return List.of();

        // 현재 학기 개설 과목과 교집합
        Set<String> openNow = new HashSet<>(baseCodes);
        List<String> candidate = recommendedIds.stream()
                .filter(openNow::contains)
                .collect(Collectors.toList());
        if (candidate.isEmpty()) return List.of();

        List<String> finalCodes;
        if (mode == RecommendedLectureExtractor.ExtractMode.HAVE_TO) {
            List<String> alreadyTaken = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, candidate);
            Set<String> takenSet = new HashSet<>(alreadyTaken);
            finalCodes = candidate.stream()
                    .filter(c -> !takenSet.contains(c))
                    .collect(Collectors.toList());
        } else {
            finalCodes = candidate;
        }
        if (finalCodes.isEmpty()) return List.of();
        return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, finalCodes);
    }
}