package com.plzgraduate.myongjigraduatebe.log.application.service;

import com.plzgraduate.myongjigraduatebe.log.application.port.LogInvalidLecturePort;
import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;


class LogInvalidLectureServiceTest {

    private LogInvalidLecturePort logInvalidLecturePort;
    private LogInvalidLectureService logInvalidLectureService;

    @BeforeEach
    void setUp() {
        logInvalidLecturePort = mock(LogInvalidLecturePort.class);
        logInvalidLectureService = new LogInvalidLectureService(logInvalidLecturePort);
    }

    @Test
    void 로그가_정상적으로_저장된다() {
        // given
        InvalidTakenLectureLog log = InvalidTakenLectureLog.builder()
                .studentNumber("60231215")
                .lectureCode("UNKNOWN02101")
                .lectureName("없는강의")
                .year(2023)
                .semester(1)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        logInvalidLectureService.log(log);

        // then
        verify(logInvalidLecturePort, times(1)).saveLog(log); // 정확히 1회 호출되었는지 확인
    }
}