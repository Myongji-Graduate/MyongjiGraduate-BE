package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularLecturesPageResponse {

    @Getter
    @Builder
    public static class PageInfo {
        private final String nextCursor;
        private final boolean hasMore;
        private final int pageSize;
    }

    private final List<GetPopularLectureResponse> lectures;
    private final PageInfo pageInfo;

    public static PopularLecturesPageResponse of(List<GetPopularLectureResponse> items, int limit) {
        boolean hasMore = items.size() > limit;
        List<GetPopularLectureResponse> page = hasMore ? items.subList(0, limit) : items;
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
