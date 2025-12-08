package com.plzgraduate.myongjigraduatebe.lecture.application.port;

public interface CoreCultureMembershipPort {

    /**
     * 해당 강의가 핵심교양(CORE_CULTURE)에 속하는지 여부.
     */
    boolean isCoreLecture(String lectureId);
}


