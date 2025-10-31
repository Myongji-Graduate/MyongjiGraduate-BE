package com.plzgraduate.myongjigraduatebe.lecturedetail.application.port;

import com.plzgraduate.myongjigraduatebe.lecturedetail.domain.model.LectureInfo;

import java.util.List;


public interface FindLectureInfoPort {

    List<LectureInfo> findBySubject(String subject);
}
