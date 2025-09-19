package com.plzgraduate.myongjigraduatebe.lecture.api.dto.response;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopularLecturesByCategoryResponse {

    private final PopularLectureCategory categoryName;
    private final List<GetPopularLectureResponse> lectures;
    private final PageInfo pageInfo;

    @Getter
    @Builder
    public static class PageInfo {
        private final String nextCursor;
        private final boolean hasMore;
        private final int pageSize;
    }

    public static PopularLecturesByCategoryResponse of(
            PopularLectureCategory categoryName,
            List<GetPopularLectureResponse> items,
            int limit
    ) {
        boolean hasMore = items.size() > limit;
        List<GetPopularLectureResponse> page = hasMore ? items.subList(0, limit) : items;
        String nextCursor = hasMore ? page.get(page.size() - 1).getId() : null;

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
