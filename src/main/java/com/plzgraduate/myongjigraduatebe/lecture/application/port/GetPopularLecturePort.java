package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.lecture.domain.model.Lecture;

import java.util.List;

public interface GetPopularLecturePort {
    List<FindPopularLectureDto> getPopularLecturesByTotalCount();
}
