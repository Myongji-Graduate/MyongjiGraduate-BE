package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularLecturesPageResponse {

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

    @Schema(description = "인기 과목 목록")
    private final List<PopularLectureResponse> lectures;
    @Schema(description = "페이지 정보")
    private final PageInfo pageInfo;

    public static PopularLecturesPageResponse of(List<PopularLectureResponse> items, int limit) {
        boolean hasMore = items.size() > limit;
        List<PopularLectureResponse> page = hasMore ? items.subList(0, limit) : items;
        String nextCursor = hasMore ? page.get(page.size() - 1).getId() : null;

        return PopularLecturesPageResponse.builder()
                .lectures(page)
                .pageInfo(PageInfo.builder()
                        .nextCursor(nextCursor)
                        .hasMore(hasMore)
                        .pageSize(limit)
                        .build())
                .build();
    }
}
