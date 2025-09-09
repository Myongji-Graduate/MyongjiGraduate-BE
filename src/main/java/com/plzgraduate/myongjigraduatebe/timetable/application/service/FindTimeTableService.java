package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.takenlecture.application.port.FindTakenLecturePort;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.FindTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.user.application.usecase.find.FindUserUseCase;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
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

    @Override
    public List<Timetable> findByYearAndSemester(int year, int semester) {
        return timetablePort.findByYearAndSemester(year, semester);
    }

    @Override
    public List<Timetable> findByKeyword(int year, int semester, String keyword) {
        return timetablePort.findByKeyword(year, semester, keyword);
    }
//
//    @Override
//    public List<Timetable> searchByCondition(int year, int semester,TimetableSearchConditionRequest condition) {
//        return timetablePort.searchByCondition(year, semester, condition);
//    }
//
//    public enum FilterType { TAKEN, NOT_TAKEN }
//
//    /**
//     * 해당 학기 전체 시간표에서 사용자의 이수/미이수 과목만 필터링
//     *
//     * @param userId   사용자 PK
//     * @param year     연도
//     * @param semester 학기
//     * @param filterType TAKEN or NOT_TAKEN
//     */
//    public List<Timetable> filterByTakenStatus(
//            Long userId,
//            int year,
//            int semester,
//            FilterType filterType
//    ) {
//        // 1) 학기 전체 과목 코드 모집단
//        List<String> baseCodes = timetablePort.findLectureCodesByYearAndSemester(year, semester);
//        if (baseCodes.isEmpty()) {
//            return List.of();
//        }
//
//        // 2) 사용자가 이수한 코드 조회
//        List<String> takenCodes = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, baseCodes);
//
//        // 3) 필터 유형에 따라 시간표 조회
//        if (filterType == FilterType.TAKEN) {
//            if (takenCodes.isEmpty()) return List.of();
//            return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, takenCodes);
//        } else { // NOT_TAKEN
//            if (takenCodes.isEmpty()) {
//                // 사용자가 아무 과목도 안 들었으면 전체가 미이수
//                return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, baseCodes);
//            }
//            return timetablePort.findByYearSemesterAndLectureCodeNotIn(year, semester, takenCodes);
//        }
//    }
//
//    @Override
//    public List<Timetable> searchCombined(
//            Long userId,
//            int year,
//            int semester,
//            TakenFilter filter,
//            TimetableSearchConditionRequest condition,
//            boolean restrictToMajorAndCommons
//    ) {
//        String userMajor = null;
//        if (filter != TakenFilter.ALL || restrictToMajorAndCommons) {
//            // 이수/미이수 또는 제한 플래그가 있으면 사용자 조회
//            User user = findUserUseCase.findUserById(userId);
//            userMajor = user.getPrimaryMajor();
//        }
//
//        return timetablePort.searchCombined(
//                userId, year, semester, filter, condition, restrictToMajorAndCommons, userMajor);
//    }

    @Override
    public List<Timetable> searchCombined(
            Long userId,
            int year,
            int semester,
            TakenFilter filter,
            TimetableSearchConditionRequest condition,
            GraduationCategory recommendedCategory
    ) {
        // 1) 기본 후보군 (조건 있으면 QDSL, 없으면 전체)
        List<Timetable> base = (condition == null)
                ? timetablePort.findByYearAndSemester(year, semester)
                : timetablePort.searchByCondition(year, semester, condition);

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
            return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, taken);
        }

        // NOT_TAKEN = haveToLectures 기반 추천 미이수
        if (filter == TakenFilter.NOT_TAKEN) {
            if (recommendedCategory == null) {
                throw new IllegalArgumentException("recommendedCategory is required when filter=NOT_TAKEN");
            }

            // (a) haveToLectures에서 추천 ID
            List<String> recommendedIds = recommendedExtractor.extractRecommendedLectureIds(userId, recommendedCategory);
            if (recommendedIds.isEmpty()) return List.of();

            // (b) 현재 학기 개설 과목과 교집합
            Set<String> openNow = new HashSet<>(baseCodes);
            List<String> recommendedOpen = recommendedIds.stream().filter(openNow::contains).collect(Collectors.toList());
            if (recommendedOpen.isEmpty()) return List.of();

            // (c) 이미 이수한 과목 제거
            List<String> alreadyTaken = findTakenLecturePort.findTakenLectureIdsByUserAndCodes(userId, recommendedOpen);
            Set<String> takenSet = new HashSet<>(alreadyTaken);
            List<String> finalCodes = recommendedOpen.stream()
                    .filter(c -> !takenSet.contains(c))
                    .collect(Collectors.toList());

            if (finalCodes.isEmpty()) return List.of();
            return timetablePort.findByYearSemesterAndLectureCodeIn(year, semester, finalCodes);
        }

        // safety
        return base;
    }
}
