package com.plzgraduate.myongjigraduatebe.lecturedetail.api;

import com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response.SearchLectureReviewResponse;
import com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "SearchLectureReview", description = "강좌명과 교수명을 통해 강좌평을 조회하는 API")
public interface SearchLectureReviewApiPresentation {

    @Operation(
            summary = "강좌평 페이징 조회",
            description = "subject(과목명)과 professor(교수명)로 강좌평을 최신순(ID DESC) 페이징 조회합니다."
    )
    SliceResponse<SearchLectureReviewResponse> searchLectureReview(
            @Parameter(description = "과목명", required = true)
            @RequestParam String subject,

            @Parameter(description = "교수명", required = true)
            @RequestParam String professor,

            @ParameterObject
            @PageableDefault(size = 5, sort = "id", direction = Sort.Direction.DESC)
            Pageable pageable
    );
}
