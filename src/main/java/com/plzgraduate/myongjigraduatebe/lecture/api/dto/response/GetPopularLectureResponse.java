package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class GetPopularLectureResponse {
    private final String id;
    private final String name;
    private final int credit;
    private final double averageRating; // 현재 0.0로 채움(별점 소스 붙이면 갱신)
    private final long totalCount;
    private final PopularLectureCategory categoryName;

    @Builder
    public GetPopularLectureResponse(String id, String name, int credit, double averageRating, long totalCount, PopularLectureCategory categoryName) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.averageRating = averageRating;
        this.totalCount = totalCount;
        this.categoryName = categoryName;
    }

    public static GetPopularLectureResponse from(
            FindPopularLectureDto findPopularLectureDto,
            double averageRating
    ) {
        return GetPopularLectureResponse.builder()
                .id(findPopularLectureDto.getLectureId())
                .name(findPopularLectureDto.getLectureName())
                .credit(findPopularLectureDto.getCredit())
                .averageRating(averageRating)
                .totalCount(findPopularLectureDto.getTotalCount())
                .categoryName(findPopularLectureDto.getCategoryName())
                .build();
    }
}
