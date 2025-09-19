package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;

import java.util.List;

public interface GetPopularLecturePort {
    List<FindPopularLectureDto> getPopularLecturesByTotalCount();
    List<FindPopularLectureDto> getLecturesByCategory(String major, int entryYear, PopularLectureCategory category, int limit, String cursor);

    List<PopularLecturesInitResponse.SectionMeta> getSections(String major, int entryYear);
}
