package com.plzgraduate.myongjigraduatebe.takenlecture.application.port;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

/** Scalar revisions and the current user are read in the same DB snapshot and round trip. */
public interface TakenLectureReadContextPort {
    ReadContext findReadContext(Long userId);

    record ReadContext(User user, long revision, long catalogRevision) { }
}
