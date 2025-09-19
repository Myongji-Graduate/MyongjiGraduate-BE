package com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.lecture.application.usecase.dto.FindPopularLectureDto;
import com.plzgraduate.myongjigraduatebe.takenlecture.infrastructure.adapter.persistence.entity.TakenLectureJpaEntity;

import java.util.List;

public interface TakenLectureQueryRepositoryCustom {
    List<FindPopularLectureDto> getPopularLecturesByTotalCount();
}
