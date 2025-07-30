package com.plzgraduate.myongjigraduatebe.log.application.usecase;

import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;

public interface LogInvalidLectureUseCase {
    void log(InvalidTakenLectureLog log);
}
