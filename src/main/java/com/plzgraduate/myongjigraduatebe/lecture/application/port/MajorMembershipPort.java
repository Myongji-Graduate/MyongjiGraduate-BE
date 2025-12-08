package com.plzgraduate.myongjigraduatebe.lecture.application.port;

import java.util.List;
import java.util.Set;

public interface MajorMembershipPort {

    /**
     * 해당 강의가 어떤 전공(Major)에라도 소속되어 있는지 여부.
     */
    boolean isMajorLecture(String lectureId);

    /**
     * 여러 강의 ID 중 전공에 소속된 강의 ID 목록을 일괄 조회.
     */
    Set<String> findMajorLectureIds(List<String> lectureIds);
}


