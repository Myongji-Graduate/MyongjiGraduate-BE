package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularLecturesService implements PopularLecturesUseCase {

    private final PopularLecturePort popularLecturePort;
    

    @Override
    public PopularLecturesInitResponse getInitPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    ) {
        // 카테고리별 전체 개수
        List<PopularLecturesInitResponse.SectionMeta> sections =
                popularLecturePort.getSections(major, entryYear);

        // 데이터가 전혀 없는 경우: 빈 리스트로 안전 반환
        if (sections.isEmpty()) {
            return PopularLecturesInitResponse.of(
                    sections,
                    PopularLectureCategory.ALL,
                    List.of(),
                    limit
            );
        }

        // 첫 번째 카테고리
        PopularLectureCategory firstCategory = sections.getFirst().getCategoryName();

        // 해당 카테고리 강의 목록
        List<PopularLectureDto> lectureDtos =
                popularLecturePort.getLecturesByCategory(major, entryYear, firstCategory, limit, cursor);

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .toList();

        return PopularLecturesInitResponse.of(
                sections,
                firstCategory,
                lectures,
                limit
        );
    }

    @Override
    public PopularLecturesByCategoryResponse getPopularLecturesByCategory(
            String major,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    ) {

        List<PopularLectureDto> lectureDtos =
                popularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor);

        if (lectureDtos.isEmpty()) {
            throw new NoSuchElementException(ErrorCode.NON_EXISTED_LECTURE.toString());
        }

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .toList();

        return PopularLecturesByCategoryResponse.of(
                category,
                lectures,
                limit
        );
    }

    @Override
    public PopularLecturesByCategoryResponse getDefaultPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    ) {
        // Avoid double work: probe fixed category order and return first non-empty page
        PopularLectureCategory[] order = new PopularLectureCategory[] {
                PopularLectureCategory.BASIC_ACADEMICAL_CULTURE,
                PopularLectureCategory.CORE_CULTURE,
                PopularLectureCategory.COMMON_CULTURE,
                PopularLectureCategory.MANDATORY_MAJOR,
                PopularLectureCategory.ELECTIVE_MAJOR
        };

        for (PopularLectureCategory cat : order) {
            List<PopularLectureDto> lectureDtos =
                    popularLecturePort.getLecturesByCategory(major, entryYear, cat, limit, cursor);
            if (!lectureDtos.isEmpty()) {
                List<PopularLectureResponse> lectures = lectureDtos.stream()
                        .map(PopularLectureResponse::from)
                        .toList();
                return PopularLecturesByCategoryResponse.of(cat, lectures, limit);
            }
        }
        throw new NoSuchElementException(ErrorCode.NON_EXISTED_LECTURE.toString());
    }

    @Override
    public PopularLecturesByCategoryResponse getPopularLectures(
            String major,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    ) {
        if (category == PopularLectureCategory.ALL) {
            return getDefaultPopularLectures(major, entryYear, limit, cursor);
        }
        return getPopularLecturesByCategory(major, entryYear, category, limit, cursor);
    }
}
