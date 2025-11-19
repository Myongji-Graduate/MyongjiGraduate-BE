package com.plzgraduate.myongjigraduatebe.lecture.application.service;

import com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.PopularLecturePort;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PopularLecturesService implements PopularLecturesUseCase {

    private final PopularLecturePort popularLecturePort;
    

    @Override
    public List<PopularLectureDto> getPopularLecturesByTotalCount() {
        return popularLecturePort.getPopularLecturesByTotalCount();
    }

    @Override
    public PopularLecturesPageResponse getPopularLectures(int limit, String cursor) {
        List<PopularLectureDto> slice = popularLecturePort.getPopularLecturesSlice(limit, cursor);

        List<PopularLectureResponse> pageItems = slice.stream()
                .map(PopularLectureResponse::from)
                .collect(Collectors.toUnmodifiableList());

        if (cursor == null && pageItems.isEmpty()) {
            throw new NoSuchElementException(ErrorCode.NO_POPULAR_LECTURES.toString());
        }

        return PopularLecturesPageResponse.of(pageItems, limit);
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
                popularLecturePort.getSections(major, entryYear);

        // 초기 섹션 총합이 0이면 404 + 표준 에러코드
        long totalSum = sections.stream().mapToLong(PopularLecturesInitResponse.SectionMeta::getTotal).sum();
        if (totalSum == 0) {
            throw new NoSuchElementException(com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NO_POPULAR_LECTURES.toString());
        }

        // 첫 번째 카테고리
        PopularLectureCategory firstCategory = sections.get(0).getCategoryName();

        // 해당 카테고리 강의 목록
        List<PopularLectureDto> lectureDtos =
                popularLecturePort.getLecturesByCategory(major, entryYear, firstCategory, limit, cursor);

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .collect(Collectors.toUnmodifiableList());

        // 카테고리=ALL 초기 primeSection 강의가 0건이어도(이상 케이스) 404 처리
        if (cursor == null && lectures.isEmpty()) {
            throw new NoSuchElementException(com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NO_POPULAR_LECTURES.toString());
        }

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

        List<PopularLectureResponse> lectures = lectureDtos.stream()
                .map(PopularLectureResponse::from)
                .collect(Collectors.toUnmodifiableList());

        // 초기 조회(cursor 없음)인데 해당 카테고리 결과가 0건이면 404 + 표준 에러코드
        if (cursor == null && lectures.isEmpty()) {
            throw new NoSuchElementException(com.plzgraduate.myongjigraduatebe.core.exception.ErrorCode.NO_POPULAR_LECTURES.toString());
        }

        return PopularLecturesByCategoryResponse.of(
                category,
                lectures,
                limit
        );
    }
}
