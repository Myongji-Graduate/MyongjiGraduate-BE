package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.GetPopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.GetPopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.GetPopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPopularLecturesService implements GetPopularLecturesUseCase {

    private final GetPopularLecturePort getPopularLecturePort;

    @Override
    public List<FindPopularLectureDto> getPopularLecturesByTotalCount() {
        return getPopularLecturePort.getPopularLecturesByTotalCount();
    }

    @Override
    public PopularLecturesInitResponse getInitPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    ) {
        // 카테고리별 전체 개수
        List<PopularLecturesInitResponse.SectionMeta> sections =
                getPopularLecturePort.getSections(major, entryYear);

        // 첫 번째 카테고리
        PopularLectureCategory firstCategory = sections.get(0).getCategoryName();

        // 해당 카테고리 강의 목록

        List<GetPopularLectureResponse> lectures =
                getPopularLecturePort.getLecturesByCategory(major, entryYear, firstCategory, limit, cursor).stream()
                        .map(dto -> GetPopularLectureResponse.from(dto, 0.0)) // 별점은 아직 없으므로 0.0
                        .collect(Collectors.toUnmodifiableList());

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

        List<GetPopularLectureResponse> lectures =
                getPopularLecturePort.getLecturesByCategory(major, entryYear, category, limit, cursor).stream()
                        .map(dto -> GetPopularLectureResponse.from(dto, 0.0))
                        .collect(Collectors.toUnmodifiableList());

        return PopularLecturesByCategoryResponse.of(
                category,
                lectures,
                limit
        );
    }
}
