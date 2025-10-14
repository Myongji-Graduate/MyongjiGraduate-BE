package com.plzgraduate.myongjigraduatebe.lecturedetail.application.usecase;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;


public interface FindLectureInfoUseCase {

    LectureInfo findLectureInfoBySubjectAndProfessor(String subject, String professor);
}
