package com.plzgraduate.myongjigraduatebe.lecture.application.port;

public interface MajorMembershipPort {

    /**
     * 해당 강의가 어떤 전공(Major)에라도 소속되어 있는지 여부.
     */
    boolean isMajorLecture(String lectureId);
}


