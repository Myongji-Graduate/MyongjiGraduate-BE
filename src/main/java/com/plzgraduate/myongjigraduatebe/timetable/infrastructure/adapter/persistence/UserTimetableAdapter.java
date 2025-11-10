package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.UserTimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.TimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.UserTimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.UserTimetableRepository;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class UserTimetableAdapter implements UserTimetablePort {
    private final UserTimetableRepository userTimetableRepository;
    private final UserTimetableMapper mapper;
    private final EntityManager em;
    private final TimetableMapper timetableMapper;

    @Override
    @Transactional
    public void deleteByUserAndSemester(Long userId, int year, int semester) {
        userTimetableRepository.deleteByUser_IdAndYearAndSemester(userId, year, semester);
    }

    @Override
    @Transactional
    public void saveAll(List<UserTimetable> userTimetables) {
        List<UserTimetableJpaEntity> entities = userTimetables.stream()
                .map(ut -> {
                    UserJpaEntity userRef = em.getReference(UserJpaEntity.class, ut.getUserId());
                    TimetableJpaEntity ttRef = em.getReference(TimetableJpaEntity.class, ut.getTimetableId());
                    return mapper.mapToJpaEntity(ut, userRef, ttRef);
                })
                .collect(Collectors.toList());

        userTimetableRepository.saveAll(entities);
    }


    @Override
    @Transactional(readOnly = true)
    public List<Timetable> findTimetablesByUserAndSemester(Long userId, int year, int semester) {
        return userTimetableRepository.findTimetablesOfUser(userId, year, semester)
                .stream()
                .map(timetableMapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }

}