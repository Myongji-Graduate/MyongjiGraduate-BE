package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.List;
import java.util.Set;

public interface BasicCultureMembershipPort {

    /**
     * 해당 강의가 학문기초교양(BASIC_ACADEMICAL_CULTURE)에 속하는지 여부.
     */
    boolean isBasicLecture(String lectureId);

    /**
     * 여러 강의 ID 중 학문기초교양에 속한 강의 ID 목록을 일괄 조회.
     */
    Set<String> findBasicLectureIds(List<String> lectureIds);
}


