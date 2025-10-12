package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
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

@Tag(name = "GetPopularLecture", description = "인기 과목 조회하는 API")
public interface PopularLectureApiPresentation {

    PopularLecturesPageResponse getPopularLectures(
            @RequestParam(defaultValue = "10") int limit);

    @Operation(summary = "인기 과목 카테고리별 조회",
            description = "category 파라미터가 없으면 초기 섹션 응답을, 지정하면 해당 카테고리 페이지를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(oneOf = {
                                    PopularLecturesInitResponse.class,
                                    PopularLecturesByCategoryResponse.class
                            }),
                            examples = {
                                    @ExampleObject(
                                            name = "init",
                                            summary = "초기 조회 (sections + primeSection)",
                                            value = "{\n  \"sections\": [\n    { \"categoryName\": \"학문기초교양\", \"total\": 15 },\n    { \"categoryName\": \"핵심교양\", \"total\": 8 },\n    { \"categoryName\": \"공통교양\", \"total\": 12 },\n    { \"categoryName\": \"일반교양\", \"total\": 4 },\n    { \"categoryName\": \"전공필수\", \"total\": 6 },\n    { \"categoryName\": \"전공선택\", \"total\": 21 }\n  ],\n  \"primeSection\": {\n    \"categoryName\": \"학문기초교양\",\n    \"lectures\": [\n      {\n        \"id\": \"KMA02137\",\n        \"name\": \"4차산업혁명시대의진로선택\",\n        \"credit\": 2,\n        \"averageRating\": 4.5,\n        \"totalCount\": 13292,\n        \"categoryName\": \"학문기초교양\"\n      },\n      {\n        \"id\": \"KMA02108\",\n        \"name\": \"영어회화1\",\n        \"credit\": 1,\n        \"averageRating\": 4.2,\n        \"totalCount\": 13292,\n        \"categoryName\": \"학문기초교양\"\n      }\n    ],\n    \"pageInfo\": {\n      \"nextCursor\": \"KMA02108\",\n      \"hasMore\": true,\n      \"pageSize\": 10\n    }\n  }\n}"
                                    ),
                                    @ExampleObject(
                                            name = "category",
                                            summary = "필터 적용 (특정 카테고리 페이지)",
                                            value = "{\n  \"categoryName\": \"전공필수\",\n  \"lectures\": [\n    {\n      \"id\": \"KMA05155\",\n      \"name\": \"데이터베이스\",\n      \"credit\": 3,\n      \"averageRating\": 4.8,\n      \"totalCount\": 13292,\n      \"categoryName\": \"전공필수\"\n    },\n    {\n      \"id\": \"KMA05156\",\n      \"name\": \"운영체제\",\n      \"credit\": 3,\n      \"averageRating\": 4.7,\n      \"totalCount\": 13292,\n      \"categoryName\": \"전공필수\"\n    }\n  ],\n  \"pageInfo\": {\n    \"nextCursor\": \"KMA05156\",\n    \"hasMore\": true,\n    \"pageSize\": 10\n  }\n}"
                                    )
                            }
                    )
            )
    })
    ResponseEntity<?> getPopularLecturesByCategory(
            @Parameter(description = "전공(정확한 전공명)", example = "응용소프트웨어전공")
            @RequestParam("major") String majors,
            @Parameter(description = "입학년도(2자리)", example = "20")
            @RequestParam("entryYear") int entryYear,
            @Parameter(description = "카테고리(한글 또는 enum)", example = "MANDATORY_MAJOR")
            @RequestParam(value = "category", required = false) PopularLectureCategory category,
            @Parameter(description = "페이지 크기", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @Parameter(description = "커서(마지막 항목 id)", example = "KMA05155")
            @RequestParam(value = "cursor", required = false) String cursor
    );
}
