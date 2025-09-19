package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.GetPopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesByCategoryResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.GetPopularLecturesUseCase;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures/popular")
@Validated
public class GetPopularLectureController implements GetPopularLectureApiPresentation,GetPopularLectureByCategoryApiPresentation {
    private final GetPopularLecturesUseCase getPopularLecturesUseCase;

    @Override
    @GetMapping
    public PopularLecturesPageResponse getPopularLectures(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<GetPopularLectureResponse> responses =
                getPopularLecturesUseCase.getPopularLecturesByTotalCount().stream()
                .map(dto -> GetPopularLectureResponse.from(dto, 0.0))
                .toList();

        return PopularLecturesPageResponse.of(responses, limit);
    }

    @Override
    @GetMapping("/by-category")
    public ResponseEntity<?> getPopularLecturesByCategory(
            @RequestParam("major") String major,
            @RequestParam("entryYear") int entryYear,
            @RequestParam(value = "category", required = false) PopularLectureCategory category,
            @RequestParam(value = "limit", defaultValue = "10") int limit,
            @RequestParam(value = "cursor", required = false) String cursor) {
        if (category == null) {
            // 최초 호출: sections + primeSection
            PopularLecturesInitResponse initResponse =
                    getPopularLecturesUseCase.getInitPopularLectures(major, entryYear, limit, cursor);
            return ResponseEntity.ok(initResponse);
        } else {
            // 카테고리 지정 호출: categoryName + lectures + pageInfo
            PopularLecturesByCategoryResponse categoryResponse =
                    getPopularLecturesUseCase.getPopularLecturesByCategory(major, entryYear, category, limit, cursor);
            return ResponseEntity.ok(categoryResponse);
        }
    }
}
