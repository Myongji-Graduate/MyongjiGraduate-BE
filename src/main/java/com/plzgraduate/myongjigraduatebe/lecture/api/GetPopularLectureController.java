package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.core.meta.WebAdapter;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.GetPopularLectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.GetPopularLecturesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@WebAdapter
@RequiredArgsConstructor
@RequestMapping("/api/v1/lectures/popular")
@Validated
public class GetPopularLectureController implements GetPopularLectureApiPresentation {
    private final GetPopularLecturesUseCase getPopularLecturesUseCase;

    @Override
    public PopularLecturesPageResponse getPopularLectures(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<GetPopularLectureResponse> responses =
                getPopularLecturesUseCase.getPopularLecturesByTotalCount().stream()
                .map(dto -> GetPopularLectureResponse.from(dto, 0.0))
                .toList();

        return PopularLecturesPageResponse.of(responses, limit);
    }
}
