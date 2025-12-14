package com.plzgraduate.myongjigraduatebe.lecture.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
 
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;

public interface PopularLecturesUseCase {

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

    /**
     * category=ALL 요청 시, 섹션을 내려주지 않고 서버가 기본 카테고리를 선택하여
     * PopularLecturesByCategoryResponse 형태로 반환
     */
    PopularLecturesByCategoryResponse getDefaultPopularLectures(
            String major,
            int entryYear,
            int limit,
            String cursor
    );

    /**
     * 통합 진입점: category가 ALL이면 getDefaultPopularLectures, 아니면 getPopularLecturesByCategory로 라우팅
     */
    PopularLecturesByCategoryResponse getPopularLectures(
            String major,
            int entryYear,
            PopularLectureCategory category,
            int limit,
            String cursor
    );
}
