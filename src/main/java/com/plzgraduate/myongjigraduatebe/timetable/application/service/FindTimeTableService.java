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
        // 1) 기본 후보군 (조건 있으면 QDSL, 없으면 전체)
        List<Timetable> base = (condition == null)
                ? timetablePort.findByYearAndSemester(year, semester)
                : timetablePort.searchByCondition(year, semester, campus, condition);

        if (base.isEmpty()) return List.of();

        if (filter == TakenFilter.ALL) {
            return base;
        }

        // 코드 집합
        List<String> baseCodes = base.stream()
                .map(Timetable::getLectureCode)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        if (filter == TakenFilter.TAKEN) {
            List<String> taken = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, baseCodes);
            if (taken.isEmpty()) return List.of();
            return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, taken);
        }

        // NOT_TAKEN = haveToLectures 기반 추천 미이수
        if (filter == TakenFilter.NOT_TAKEN) {

            // 1) 추천 소스 카테고리 결정
            List<GraduationCategory> sourceCategories;
            if (recommendedCategory != null) {
                sourceCategories = List.of(recommendedCategory);
            } else {
                // 기본 카테고리 5개
                List<GraduationCategory> baseCategories = List.of(
                        GraduationCategory.PRIMARY_MANDATORY_MAJOR,
                        GraduationCategory.PRIMARY_ELECTIVE_MAJOR,
                        GraduationCategory.COMMON_CULTURE,
                        GraduationCategory.CORE_CULTURE,
                        GraduationCategory.PRIMARY_BASIC_ACADEMICAL_CULTURE
                );

                // 가변 목록으로 시작
                List<GraduationCategory> dynamic = new ArrayList<>(baseCategories);

                // 사용자 학적에 따라 추가 카테고리 부여
                switch (user.getStudentCategory()) {
                    case SUB_MAJOR:
                        dynamic.add(GraduationCategory.SUB_MAJOR);
                        break;
                    case DUAL_MAJOR:
                        dynamic.add(GraduationCategory.DUAL_ELECTIVE_MAJOR);
                        dynamic.add(GraduationCategory.DUAL_BASIC_ACADEMICAL_CULTURE);
                        break;
                    default:
                        // NORMAL 등 기본 케이스
                        break;
                }

                sourceCategories = dynamic;
            }

            // 2) haveToLectures 기반 추천 id 수집(카테고리 합산) + 중복 제거
            Set<String> recommendedIds = new HashSet<>();
            for (GraduationCategory cat : sourceCategories) {
                List<String> ids = recommendedExtractor.extractRecommendedLectureIds(userId, cat);
                if (ids != null && !ids.isEmpty()) {
                    recommendedIds.addAll(ids);
                }
            }
            if (recommendedIds.isEmpty()) return List.of();

            // 3) 현재 학기 개설 과목과 교집합
            Set<String> openNow = new HashSet<>(baseCodes);
            List<String> recommendedOpen = recommendedIds.stream()
                    .filter(openNow::contains)
                    .collect(Collectors.toList());
            if (recommendedOpen.isEmpty()) return List.of();

            // 4) 이미 이수한 과목 제거
            List<String> alreadyTaken = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, recommendedOpen);
            Set<String> takenSet = new HashSet<>(alreadyTaken);
            List<String> finalCodes = recommendedOpen.stream()
                    .filter(c -> !takenSet.contains(c))
                    .collect(Collectors.toList());

            if (finalCodes.isEmpty()) return List.of();
            return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, campus, finalCodes);
        }
        return base;
    }
}

