package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.core.meta.PersistenceAdapter;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.TakenFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.TimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.TimetableQueryDslRepository;
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
    public List<Timetable> findByKeyword(int year, int semester, String keyword) {
        return repository.findByYearAndSemesterAndNameContaining(year, semester, keyword)
                .stream().map(mapper::mapToDomainEntity).collect(Collectors.toList());
    }

    @Override
    public List<Timetable> searchByCondition(int year, int semester, TimetableSearchConditionRequest condition) {
        return timetableQueryRepository.searchByCondition(year, semester, condition).stream()
                .map(mapper::mapToDomainEntity)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<String> findLectureCodesByYearAndSemester(int year, int semester) {
//        return repository.findAllByYearAndSemester(year, semester)
//                .stream()
//                .map(timetableEntity -> timetableEntity.getLectureCode())
//                .collect(Collectors.toList());
//    }

//    @Override
//    public List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, List<String> codes) {
//        return repository.findByYearAndSemesterAndLectureCodeIn(year, semester, codes)
//                .stream()
//                .map(mapper::mapToDomainEntity)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Timetable> findByYearSemesterAndLectureCodeNotIn(int year, int semester, List<String> codes) {
//        return repository.findByYearAndSemesterAndLectureCodeNotIn(year, semester, codes)
//                .stream()
//                .map(mapper::mapToDomainEntity)
//                .collect(Collectors.toList());
//    }

//    @Override
//    public List<Timetable> searchCombined(
//            Long userId,
//            int year,
//            int semester,
//            TakenFilter filter,
//            TimetableSearchConditionRequest condition,
//            boolean restrictToMajorAndCommons,
//            String userMajor
//    ) {
//        return queryDslRepository.searchCombined(
//                        year, semester, condition, filter, userId, restrictToMajorAndCommons, userMajor)
//                .stream()
//                .map(mapper::mapToDomainEntity)
//                .collect(Collectors.toList());
//    }


    @Override
    public List<String> findLectureCodesByYearAndSemester(int year, int semester) {
        return repository.findAllByYearAndSemester(year, semester)
                .stream()
                .map(e -> e.getLectureCode())
                .collect(Collectors.toList());
    }

    @Override
    public List<Timetable> findByYearSemesterAndLectureCodeIn(int year, int semester, List<String> codes) {
        return repository.findByYearAndSemesterAndLectureCodeIn(year, semester, codes)
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
