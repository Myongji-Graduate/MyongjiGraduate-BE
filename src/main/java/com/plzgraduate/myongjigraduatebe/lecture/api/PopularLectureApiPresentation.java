package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "GetPopularLecture", description = "필터링 없이 인기 과목 조회하는 API")
public interface PopularLectureApiPresentation {

    PopularLecturesPageResponse getPopularLectures(
            @RequestParam(defaultValue = "10") int limit);
}
