package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;

import java.util.List;

public interface TakenLectureRepositoryCustom {
    List<FindPopularLectureDto> getPopularLecturesByTotalCount();
}
