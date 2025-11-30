package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GetPopularLecture", description = "인기 과목 카테고리별 조회 API (카테고리 고정 순서 반영)")
public interface PopularLectureApiPresentation {

    @Operation(summary = "인기 과목 카테고리별 조회",
            description = """
                    category=ALL이어도 섹션 없이 서버가 기본 카테고리를 선택해 동일한 형태로 반환합니다.
                    카테고리 고정 순서: 학문기초교양(BASIC_ACADEMICAL_CULTURE) → 핵심교양(CORE_CULTURE) → 공통교양(COMMON_CULTURE) → 전공필수(MANDATORY_MAJOR) → 전공선택(ELECTIVE_MAJOR).
                    ALL 초기 호출 응답의 categoryName을 사용해 무한 스크롤을 이어가며, hasMore=false인 경우 위 고정 순서의 다음 카테고리로 요청을 전환합니다.""")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PopularLecturesByCategoryResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "category",
                                            summary = "ALL 포함 동일 형태 반환",
                                            value = "{\n  \"categoryName\": \"전공필수\",\n  \"lectures\": [\n    {\n      \"id\": \"KMA05155\",\n      \"name\": \"데이터베이스\",\n      \"credit\": 3,\n      \"averageRating\": 4.8,\n      \"totalCount\": 13292,\n      \"categoryName\": \"전공필수\"\n    },\n    {\n      \"id\": \"KMA05156\",\n      \"name\": \"운영체제\",\n      \"credit\": 3,\n      \"averageRating\": 4.7,\n      \"totalCount\": 13292,\n      \"categoryName\": \"전공필수\"\n    }\n  ],\n  \"pageInfo\": {\n    \"nextCursor\": \"KMA05156\",\n    \"hasMore\": true,\n    \"pageSize\": 10\n  }\n}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<PopularLecturesByCategoryResponse> getPopularLecturesByCategory(
            @Parameter(description = "전공(정확한 전공명)", example = "응용소프트웨어전공")
            @RequestParam("major") String major,
            @Parameter(description = "입학년도(2자리)", example = "20")
            @RequestParam("entryYear") int entryYear,
            @Parameter(description = "카테고리(한글 또는 enum). ALL 가능. 고정 순서: BASIC_ACADEMICAL_CULTURE → CORE_CULTURE → COMMON_CULTURE → MANDATORY_MAJOR → ELECTIVE_MAJOR",
                    example = "MANDATORY_MAJOR")
            @RequestParam("category") PopularLectureCategory category,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @Parameter(description = "커서(마지막 항목 id)", example = "KMA05155")
            @RequestParam(value = "cursor", required = false) String cursor
    );
}
