package com.plzgraduate.myongjigraduatebe.log.application.service;

import com.plzgraduate.myongjigraduatebe.log.application.port.LogInvalidLecturePort;
import com.plzgraduate.myongjigraduatebe.log.application.usecase.LogInvalidLectureUseCase;
import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogInvalidLectureService implements LogInvalidLectureUseCase {

    private final LogInvalidLecturePort logInvalidLecturePort;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void log(InvalidTakenLectureLog log) {
        logInvalidLecturePort.saveLog(log);
    }
}