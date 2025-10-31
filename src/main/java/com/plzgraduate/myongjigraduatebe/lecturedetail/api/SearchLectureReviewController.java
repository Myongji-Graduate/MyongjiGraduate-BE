package com.plzgraduate.myongjigraduatebe.lecturedetail.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response.SearchLectureReviewResponse;
import com.plzgraduate.myongjigraduatebe.lecturedetail.api.dto.response.SliceResponse;
import com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase.SearchLectureReviewUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lecture-reviews")
@Validated
public class SearchLectureReviewController implements SearchLectureReviewApiPresentation{

    private final SearchLectureReviewUseCase searchLectureReviewUseCase;

    @GetMapping
    public SliceResponse<SearchLectureReviewResponse> searchLectureReview(
            @RequestParam String subject,
            @RequestParam String professor,
            @PageableDefault(page = 1, size = 5, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return SliceResponse.from(searchLectureReviewUseCase.findLectureReviewBySubjectAndProfessor(subject, professor, pageable)
                .map(SearchLectureReviewResponse::from));
    }
}
