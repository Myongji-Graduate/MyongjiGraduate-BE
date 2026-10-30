package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

public interface TakenLectureRevisionPort {
    long findRevision(Long userId);

    /** Acquire before the first snapshot read in a student's change transaction. */
    void lockForChange(Long userId);
}
