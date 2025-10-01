package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;

import java.util.List;

public interface PopularLecturesUseCase {
    List<PopularLectureDto> getPopularLecturesByTotalCount();

    /**
     * 최초 호출 (category=null)
     * - sections + primeSection 반환
     */
    PopularLecturesInitResponse getInitPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    );

    /**
     * 카테고리 지정 호출 (무한 스크롤)
     * - categoryName + lectures + pageInfo 반환
     */
    PopularLecturesByCategoryResponse getPopularLecturesByCategory(
            String majors,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    );
}
