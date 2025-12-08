package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.TimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.TimetableQueryRepository;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@PersistenceAdapter
@RequiredArgsConstructor
public class TimetablePersistenceAdapter implements TimetablePort {

    private final TimetableRepository repository;
    private final TimetableQueryRepository timetableQueryRepository;
    private final TimetableMapper mapper;

    @Override
    public List<Timetable> findByYearAndSemester(int year, int semester) {
        return repository.findAllByYearAndSemester(year, semester)
                .stream().map(mapper::mapToDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Timetable> searchByCondition(int year, int semester, CampusFilter campus, TimetableSearchConditionRequest condition) {
        return timetableQueryRepository.searchByCondition(year, semester, campus, condition).stream()
                .map(mapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findLectureCodesByYearAndSemester(int year, int semester) {
        return repository.findAllByYearAndSemester(year, semester)
                .stream()
                .map(TimetableJpaEntity::getLectureCode)
                .collect(Collectors.toList());
    }

    @Override
    public List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, CampusFilter campus, List<String> codes) {
        return repository.findByYearAndSemesterAndCampusAndLectureCodeIn(year, semester, campus.name(), codes)
                .stream()
                .map(mapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<Timetable> findByYearSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes) {
        return repository.findByYearAndSemesterAndLectureCodeNotIn(year, semester, codes)
                .stream()
                .map(mapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }
}
