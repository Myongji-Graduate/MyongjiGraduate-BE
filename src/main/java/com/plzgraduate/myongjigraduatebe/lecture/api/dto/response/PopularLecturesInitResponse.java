package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularLecturesInitResponse {

    @Schema(description = "카테고리별 섹션 메타 정보 목록")
    private final List<SectionMeta> sections;
    @Schema(description = "초기 노출 섹션 정보")
    private final PrimeSection primeSection;

    @Getter
    @Builder
    public static class SectionMeta {
        @Schema(name = "categoryName", example = "학문기초교양", description = "카테고리 한글명")
        private final PopularLectureCategory categoryName;
        @Schema(name = "total", example = "15", description = "해당 카테고리 총 개수")
        private final long total;
    }

    @Getter
    @Builder
    public static class PrimeSection {
        @Schema(name = "categoryName", example = "학문기초교양", description = "초기 노출 카테고리")
        private final PopularLectureCategory categoryName;
        @Schema(description = "초기 노출 강의 목록")
        private final List<GetPopularLectureResponse> lectures;
        @Schema(description = "페이지 정보")
        private final PageInfo pageInfo;
    }

    @Getter
    @Builder
    public static class PageInfo {
        @Schema(name = "nextCursor", example = "KMA02108", description = "다음 페이지 커서(마지막 항목 id)")
        private final String nextCursor;
        @Schema(name = "hasMore", example = "true", description = "다음 페이지 존재 여부")
        private final boolean hasMore;
        @Schema(name = "pageSize", example = "10", description = "요청한 페이지 크기")
        private final int pageSize;
    }

    public static PopularLecturesInitResponse of(
            List<SectionMeta> sections,
            PopularLectureCategory categoryName,
            List<GetPopularLectureResponse> items,
            int limit
    ) {
        boolean hasMore = items.size() > limit;
        List<GetPopularLectureResponse> page = hasMore ? items.subList(0, limit) : items;
        String nextCursor = hasMore ? page.get(page.size() - 1).getId() : null;

        return PopularLecturesInitResponse.builder()
                .sections(sections)
                .primeSection(
                        PrimeSection.builder()
                                .categoryName(categoryName)
                                .lectures(page)
                                .pageInfo(PageInfo.builder()
                                        .nextCursor(nextCursor)
                                        .hasMore(hasMore)
                                        .pageSize(limit)
                                        .build())
                                .build()
                )
                .build();
    }
}
