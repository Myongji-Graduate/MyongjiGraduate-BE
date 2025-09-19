package com.plzgraduate.myongjigraduatebe.lecture.api;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesPageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "GetPopularLecture", description = "여러 필터(학점, 단과대, 교양종류, 입학년도, 전공/교양)를 조합을 통해 인기 과목정보를 조회하는 API")
public interface GetPopularLectureApiPresentation {

    PopularLecturesPageResponse getPopularLectures(int limit);
}
