package com.plzgraduate.myongjigraduatebe.lecture.application.port;

public interface BasicCultureMembershipPort {

    /**
     * 해당 강의가 학문기초교양(BASIC_ACADEMICAL_CULTURE)에 속하는지 여부.
     */
    boolean isBasicLecture(String lectureId);
}


