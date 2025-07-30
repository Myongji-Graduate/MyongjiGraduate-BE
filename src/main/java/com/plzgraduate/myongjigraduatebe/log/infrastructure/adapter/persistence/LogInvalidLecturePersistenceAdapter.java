package com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence;


import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.log.domain.InvalidTakenLectureLog;
import com.plzgraduate.myongjigraduatebe.log.application.port.LogInvalidLecturePort;
import com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.entity.InvalidTakenLectureLogEntity;
import com.plzgraduate.myongjigraduatebe.log.infrastructure.adapter.persistence.repository.InvalidTakenLectureLogJpaRepository;
import lombok.RequiredArgsConstructor;

@PersistenceAdapter
@RequiredArgsConstructor
public class LogInvalidLecturePersistenceAdapter implements LogInvalidLecturePort{
    private final InvalidTakenLectureLogJpaRepository repository;

    @Override
    public void saveLog(InvalidTakenLectureLog log) {
        InvalidTakenLectureLogEntity entity = InvalidTakenLectureLogEntity.builder()
                .studentNumber(log.getStudentNumber())
                .lectureCode(log.getLectureCode())
                .lectureName(log.getLectureName())
                .year(log.getYear())
                .semester(log.getSemester())
                .timestamp(log.getTimestamp())
                .build();

        repository.save(entity);
    }

}
