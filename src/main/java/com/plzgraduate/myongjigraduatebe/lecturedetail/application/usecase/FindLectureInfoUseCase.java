package com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;

import java.util.List;


public interface FindLectureInfoUseCase {

    List<LectureInfo> findLectureInfoBySubject(String subject);
}
