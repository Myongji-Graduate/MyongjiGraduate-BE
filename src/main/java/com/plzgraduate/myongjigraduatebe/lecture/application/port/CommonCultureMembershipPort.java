package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.List;
import java.util.Set;

public interface CommonCultureMembershipPort {

    /**
     * 해당 강의가 공통교양(COMMON_CULTURE)에 속하는지 여부.
     */
    boolean isCommonLecture(String lectureId);

    /**
     * 여러 강의 ID 중 공통교양에 속한 강의 ID 목록을 일괄 조회.
     */
    Set<String> findCommonLectureIds(List<String> lectureIds);
}


