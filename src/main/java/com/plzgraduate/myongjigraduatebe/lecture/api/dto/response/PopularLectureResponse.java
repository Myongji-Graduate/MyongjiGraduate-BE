package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PopularLectureResponse {
    @Schema(name = "id", example = "KMA02137", description = "교과목 코드")
    private final String id;
    @Schema(name = "name", example = "4차산업혁명시대의진로선택", description = "교과목명")
    private final String name;
    @Schema(name = "credit", example = "2", description = "학점")
    private final int credit;
    @Schema(name = "averageRating", example = "4.2", description = "평균 평점")
    private final double averageRating;
    @Schema(name = "totalCount", example = "1292", description = "누적 수강 인원 수")
    private final long totalCount;
    @Schema(name = "categoryName", example = "전공필수", description = "카테고리 한글명")
    private final PopularLectureCategory categoryName;

    @Builder
    public PopularLectureResponse(String id, String name, int credit, double averageRating, long totalCount, PopularLectureCategory categoryName) {
        this.id = id;
        this.name = name;
        this.credit = credit;
        this.averageRating = averageRating;
        this.totalCount = totalCount;
        this.categoryName = categoryName;
    }

    public static PopularLectureResponse from(
            PopularLectureDto findPopularLectureDto
    ) {
        return PopularLectureResponse.builder()
                .id(findPopularLectureDto.getLectureId())
                .name(findPopularLectureDto.getLectureName())
                .credit(findPopularLectureDto.getCredit())
                .averageRating(Math.round(findPopularLectureDto.getAverageRating() * 10.0) / 10.0)
                .totalCount(findPopularLectureDto.getTotalCount())
                .categoryName(findPopularLectureDto.getCategoryName())
                .build();
    }
}
