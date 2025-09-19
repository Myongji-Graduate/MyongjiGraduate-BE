package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GetPopularLectureByCategory", description = "카테고리별 인기 과목 조회 API")
public interface GetPopularLectureByCategoryApiPresentation {

    @GetMapping("/api/v1/lectures/popular/by-category")
    ResponseEntity<?> getPopularLecturesByCategory(
            @RequestParam("major") String majors,
            @RequestParam("entryYear") int entryYear,
            @RequestParam(value = "category", required = false) PopularLectureCategory category,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "cursor", required = false) String cursor
    );
}
