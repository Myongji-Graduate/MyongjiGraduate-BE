package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.api.dto.response.PopularLecturesInitResponse;
import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.PopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.PopularLectureCategory;

import java.util.List;

public interface PopularLecturePort {
    List<PopularLectureDto> getPopularLecturesByTotalCount();
    List<PopularLectureDto> getPopularLecturesSlice(int limit, String cursor);
    List<PopularLectureDto> getLecturesByCategory(String major, int entryYear, PopularLectureCategory category, int limit, String cursor);

    List<PopularLecturesInitResponse.SectionMeta> getSections(String major, int entryYear);
}
