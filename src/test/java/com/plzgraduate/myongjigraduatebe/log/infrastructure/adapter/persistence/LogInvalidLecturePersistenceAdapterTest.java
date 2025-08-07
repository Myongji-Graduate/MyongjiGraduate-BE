package com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;
import com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.repository.InvalidTakenLectureLogJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogInvalidLecturePersistenceAdapterTest {

    @Mock
    private InvalidTakenLectureLogJpaRepository repository;

    @InjectMocks
    private LogInvalidLecturePersistenceAdapter adapter;

// LogInvalidLecturePersistenceAdapter 가 InvalidTakenLectureLog 객체를 받아서
// 내부적으로 repository.save() 를 호출하여 로그를 저장하는지 확인하는 단위 테스트
    @Test
    void shouldSaveLogEntity() {
        // given
        InvalidTakenLectureLog log = InvalidTakenLectureLog.builder()
            .studentNumber("60231215")
            .lectureCode("KMA02135")
            .lectureName("인공지능의이해")
            .year(2025)
            .semester(1)
            .timestamp(LocalDateTime.now())
            .build();

        // when
        adapter.saveLog(log);

        // then
        verify(repository, times(1)).save(any());
    }
}