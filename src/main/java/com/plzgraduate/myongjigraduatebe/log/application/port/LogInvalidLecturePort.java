package com.plzgraduate.myongjigraduatebe.log.application.port;

import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;

public interface LogInvalidLecturePort {
    void saveLog(InvalidTakenLectureLog log);
}
