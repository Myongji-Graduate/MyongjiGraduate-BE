package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory.*;

@Component
@RequiredArgsConstructor
public class LectureCategoryResolver {

    private final MajorLectureRepository majorLectureRepository;
    private final CoreCultureRepository coreCultureRepository;
    private final BasicAcademicalCultureRepository basicAcademicalCultureRepository;
    private final CommonCultureRepository commonCultureRepository;

    /**
     * 컨텍스트 없이 단순 카테고리 분류
     */
    public List<FindPopularLectureDto> attachWithoutContext(List<FindPopularLectureDto> rawLectures) {
        List<String> lectureIds = rawLectures.stream().map(FindPopularLectureDto::getLectureId).toList();

        Set<String> majorMandatoryIds =
                new HashSet<>(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 1));
        Set<String> majorElectiveIds =
                new HashSet<>(majorLectureRepository.findIdsByLectureIdInAndIsMandatory(lectureIds, 0));
        Set<String> coreCultureIds =
                new HashSet<>(coreCultureRepository.findIdsByLectureIdIn(lectureIds));
        Set<String> basicCultureIds =
                new HashSet<>(basicAcademicalCultureRepository.findIdsByLectureIdIn(lectureIds));
        Set<String> commonCultureIds =
                new HashSet<>(commonCultureRepository.findIdsByLectureIdIn(lectureIds));

        return rawLectures.stream()
                .map(dto -> FindPopularLectureDto.of(
                        dto.getLectureId(),
                        dto.getLectureName(),
                        dto.getCredit(),
                        dto.getTotalCount(),
                        resolveCategory(dto.getLectureId(),
                                majorMandatoryIds,
                                majorElectiveIds,
                                coreCultureIds,
                                basicCultureIds,
                                commonCultureIds)
                ))
                .toList();
    }

    /**
     * 특정 major + entryYear 맥락으로 카테고리 분류
     */
    public List<FindPopularLectureDto> attachWithContext(List<FindPopularLectureDto> rawLectures,
                                                         String major,
                                                         int entryYear) {
        List<String> lectureIds = rawLectures.stream().map(FindPopularLectureDto::getLectureId).toList();

        // 1) major → 소속 college
        String college = com.plzgraduate.myongjigraduatebe.user.domain.model.College
                .findBelongingCollege(major, entryYear)
                .getName();

        // 2) BASIC: 단과대 기준 필터
        Set<String> basicCultureIdsByCollege =
                new HashSet<>(basicAcademicalCultureRepository
                        .findIdsByLectureIdInAndCollegeIn(lectureIds, Set.of(college)));

        // 3) CORE / COMMON: entryYear 기준 필터
        Set<String> coreCultureIdsByYear =
                new HashSet<>(coreCultureRepository.findIdsByLectureIdInAndEntryYearBetween(lectureIds, entryYear));
        Set<String> commonCultureIdsByYear =
                new HashSet<>(commonCultureRepository.findIdsByLectureIdInAndEntryYearBetween(lectureIds, entryYear));

        // 4) MAJOR: 해당 전공 + entryYear 기준 필터
        Set<String> majorMandatoryIdsByYear =
                new HashSet<>(majorLectureRepository
                        .findIdsByLectureIdInAndMajorsInAndIsMandatoryAndEntryYearBetween(
                                lectureIds, List.of(major), 1, entryYear));
        Set<String> majorElectiveIdsByYear =
                new HashSet<>(majorLectureRepository
                        .findIdsByLectureIdInAndMajorsInAndIsMandatoryAndEntryYearBetween(
                                lectureIds, List.of(major), 0, entryYear));

        // 5) 매핑 결과 조합
        return rawLectures.stream()
                .map(dto -> FindPopularLectureDto.of(
                        dto.getLectureId(),
                        dto.getLectureName(),
                        dto.getCredit(),
                        dto.getTotalCount(),
                        resolveCategory(dto.getLectureId(),
                                majorMandatoryIdsByYear,
                                majorElectiveIdsByYear,
                                coreCultureIdsByYear,
                                basicCultureIdsByCollege,
                                commonCultureIdsByYear)
                ))
                .toList();
    }

    private PopularLectureCategory resolveCategory(String lectureId,
                                                   Set<String> majorMandatoryIds,
                                                   Set<String> majorElectiveIds,
                                                   Set<String> coreCultureIds,
                                                   Set<String> basicCultureIds,
                                                   Set<String> commonCultureIds) {
        if (majorMandatoryIds.contains(lectureId)) return MANDATORY_MAJOR;
        if (majorElectiveIds.contains(lectureId)) return ELECTIVE_MAJOR;
        if (coreCultureIds.contains(lectureId)) return CORE_CULTURE;
        if (basicCultureIds.contains(lectureId)) return BASIC_ACADEMICAL_CULTURE;
        if (commonCultureIds.contains(lectureId)) return COMMON_CULTURE;
        return NORMAL_CULTURE;
    }
}
