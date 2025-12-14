package com.plzgraduate.myongjigraduatebe.timetable.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TimetablePageResponse {

    @Schema(description = "과목 목록")
    private final List<TimetableResponse> data;

    @Schema(name = "nextPage", example = "2", description = "다음 페이지 번호 (null이면 마지막 페이지)")
    private final Integer nextPage;

    @Schema(name = "totalCount", example = "150", description = "전체 데이터 개수")
    private final Long totalCount;

    public static TimetablePageResponse of(List<TimetableResponse> data, int currentPage, int limit, long totalCount) {
        boolean hasNext = (long) currentPage * limit < totalCount;
        Integer nextPage = hasNext ? currentPage + 1 : null;

        return TimetablePageResponse.builder()
                .data(data)
                .nextPage(nextPage)
                .totalCount(totalCount)
                .build();
    }
}

