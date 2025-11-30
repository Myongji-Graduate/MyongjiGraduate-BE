package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularLecturesByCategoryResponse {

    @Schema(name = "categoryName", example = "전공필수", description = "조회한 카테고리 한글명")
    private final PopularLectureCategory categoryName;
    @Schema(description = "해당 카테고리 강의 목록")
    private final List<PopularLectureResponse> lectures;
    @Schema(description = "페이지 정보")
    private final PageInfo pageInfo;

    @Getter
    @Builder
    public static class PageInfo {
        @Schema(name = "nextCursor", example = "KMA05156", description = "다음 페이지 커서(마지막 항목 id)")
        private final String nextCursor;
        @Schema(name = "hasMore", example = "true", description = "다음 페이지 존재 여부")
        private final boolean hasMore;
        @Schema(name = "pageSize", example = "10", description = "요청한 페이지 크기")
        private final int pageSize;
    }

    public static PopularLecturesByCategoryResponse of(
            PopularLectureCategory categoryName,
            List<PopularLectureResponse> items,
            int limit
    ) {
        boolean hasMore = items.size() > limit;
        List<PopularLectureResponse> page = hasMore ? items.subList(0, limit) : items;
        String nextCursor = null;
        if (hasMore) {
            PopularLectureResponse last = page.getLast();
            nextCursor = last.getId();
        }

        return PopularLecturesByCategoryResponse.builder()
                .categoryName(categoryName)
                .lectures(page)
                .pageInfo(PageInfo.builder()
                        .nextCursor(nextCursor)
                        .hasMore(hasMore)
                        .pageSize(limit)
                        .build())
                .build();
    }
}
