package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.List;
import java.util.Set;

public interface CoreCultureMembershipPort {

    /**
     * 해당 강의가 핵심교양(CORE_CULTURE)에 속하는지 여부.
     */
    boolean isCoreLecture(String lectureId);

    /**
     * 여러 강의 ID 중 핵심교양에 속한 강의 ID 목록을 일괄 조회.
     */
    Set<String> findCoreLectureIds(List<String> lectureIds);
}


