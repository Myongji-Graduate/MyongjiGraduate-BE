package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import com.plzgraduate.myongjigraduatebe.lecture.domain.model.MajorLectureOffering;

import java.util.Optional;

public interface MajorLectureOfferingPort {
    Optional<MajorLectureOffering> findByLectureId(String lectureId);
    MajorLectureOffering save(MajorLectureOffering offering);
}
