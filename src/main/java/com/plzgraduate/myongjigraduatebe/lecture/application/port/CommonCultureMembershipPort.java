package com.plzgraduate.myongjigraduatebe.lecture.application.port;

public interface CommonCultureMembershipPort {

    /**
     * 해당 강의가 공통교양(COMMON_CULTURE)에 속하는지 여부.
     */
    boolean isCommonLecture(String lectureId);
}


