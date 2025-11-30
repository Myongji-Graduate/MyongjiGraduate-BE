package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.PopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

 

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures/popular")
@Validated
public class PopularLectureController implements PopularLectureApiPresentation {
    private final PopularLecturesUseCase popularLecturesUseCase;

    @Override
    @GetMapping("/by-category")
    public ResponseEntity<?> getPopularLecturesByCategory(
            @RequestParam("major") String major,
            @RequestParam("entryYear") int entryYear,
            @RequestParam("category") PopularLectureCategory category,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "cursor", required = false) String cursor) {
        if (category == PopularLectureCategory.ALL) {
            // 최초 호출: sections + primeSection
            PopularLecturesInitResponse initResponse =
                    popularLecturesUseCase.getInitPopularLectures(major, entryYear, limit, cursor);
            return ResponseEntity.ok(initResponse);
        } else {
            // 특정 카테고리 지정 호출: categoryName + lectures + pageInfo
            PopularLecturesByCategoryResponse categoryResponse =
                    popularLecturesUseCase.getPopularLecturesByCategory(major, entryYear, category, limit, cursor);
            return ResponseEntity.ok(categoryResponse);
        }
    }
}
