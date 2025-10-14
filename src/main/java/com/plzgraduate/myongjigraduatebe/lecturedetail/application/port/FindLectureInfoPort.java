package com.plzgraduate.myongjigraduatebe.lecturedetail.application.port;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;



public interface FindLectureInfoPort {

    LectureInfo findBySubjectAndProfessor(String subject, String professor);
}
