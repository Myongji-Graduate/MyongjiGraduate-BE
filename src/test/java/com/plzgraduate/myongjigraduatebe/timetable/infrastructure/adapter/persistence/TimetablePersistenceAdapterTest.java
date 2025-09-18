package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.request.TimetableSearchConditionRequest;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.CampusFilter;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.TimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.TimetableQueryRepository;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.TimetableRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TimetablePersistenceAdapterTest {

    @Mock private TimetableRepository repository;
    @Mock private TimetableQueryRepository queryRepository;
    @Mock private TimetableMapper mapper;

    @InjectMocks private TimetablePersistenceAdapter sut;

    @Test
    @DisplayName("findByYearAndSemester: Repository → Mapper로 매핑")
    void findByYearAndSemester_maps() {
        int year = 2025; int semester = 2;
        TimetableJpaEntity e1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity e2 = mock(TimetableJpaEntity.class);
        when(repository.findAllByYearAndSemester(year, semester)).thenReturn(List.of(e1, e2));

        Timetable d1 = mock(Timetable.class);
        Timetable d2 = mock(Timetable.class);
        when(mapper.mapToDomainEntity(e1)).thenReturn(d1);
        when(mapper.mapToDomainEntity(e2)).thenReturn(d2);

        List<Timetable> result = sut.findByYearAndSemester(year, semester);

        assertThat(result).containsExactly(d1, d2);
        verify(repository).findAllByYearAndSemester(year, semester);
        verify(mapper).mapToDomainEntity(e1);
        verify(mapper).mapToDomainEntity(e2);
    }

    @Test
    @DisplayName("findByKeyword: 이름 like 검색 → Mapper 매핑")
    void findByKeyword_maps() {
        int year = 2024; int semester = 1; String keyword = "자료구조";
        TimetableJpaEntity e = mock(TimetableJpaEntity.class);
        when(repository.findByYearAndSemesterAndNameContaining(year, semester, keyword))
                .thenReturn(List.of(e));
        Timetable d = mock(Timetable.class);
        when(mapper.mapToDomainEntity(e)).thenReturn(d);

        List<Timetable> result = sut.findByKeyword(year, semester, keyword);

        assertThat(result).containsExactly(d);
        verify(repository).findByYearAndSemesterAndNameContaining(year, semester, keyword);
        verify(mapper).mapToDomainEntity(e);
    }

    @Test
    @DisplayName("searchByCondition: QueryRepository 호출 → Mapper 매핑")
    void searchByCondition_maps() {
        int year = 2023; int semester = 2; CampusFilter campus = CampusFilter.인문;
        TimetableSearchConditionRequest cond = mock(TimetableSearchConditionRequest.class);

        TimetableJpaEntity e1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity e2 = mock(TimetableJpaEntity.class);
        when(queryRepository.searchByCondition(year, semester, campus, cond)).thenReturn(List.of(e1, e2));

        Timetable d1 = mock(Timetable.class);
        Timetable d2 = mock(Timetable.class);
        when(mapper.mapToDomainEntity(e1)).thenReturn(d1);
        when(mapper.mapToDomainEntity(e2)).thenReturn(d2);

        List<Timetable> result = sut.searchByCondition(year, semester, campus, cond);

        assertThat(result).containsExactly(d1, d2);
        verify(queryRepository).searchByCondition(year, semester, campus, cond);
        verify(mapper).mapToDomainEntity(e1);
        verify(mapper).mapToDomainEntity(e2);
    }

    @Test
    @DisplayName("findLectureCodesByYearAndSemester: JPA 엔티티에서 코드만 추출")
    void findLectureCodes_onlyCodes() {
        int year = 2025; int semester = 1;
        TimetableJpaEntity e1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity e2 = mock(TimetableJpaEntity.class);
        when(repository.findAllByYearAndSemester(year, semester)).thenReturn(List.of(e1, e2));
        when(e1.getLectureCode()).thenReturn("CSE101");
        when(e2.getLectureCode()).thenReturn("CSE102");

        List<String> codes = sut.findLectureCodesByYearAndSemester(year, semester);

        assertThat(codes).containsExactly("CSE101", "CSE102");
        verify(repository).findAllByYearAndSemester(year, semester);
        verify(e1).getLectureCode();
        verify(e2).getLectureCode();
        verifyNoInteractions(mapper, queryRepository);
    }

    @Test
    @DisplayName("findByYearSemesterAndLectureCodeIn: campus.name()으로 호출 → Mapper 매핑")
    void findByYearSemesterAndLectureCodeIn_maps() {
        int year = 2022; int semester = 2; CampusFilter campus = CampusFilter.자연;
        List<String> codes = List.of("MTH201", "PHY101");

        TimetableJpaEntity e1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity e2 = mock(TimetableJpaEntity.class);
        when(repository.findByYearAndSemesterAndCampusAndLectureCodeIn(year, semester, campus.name(), codes))
                .thenReturn(List.of(e1, e2));

        Timetable d1 = mock(Timetable.class);
        Timetable d2 = mock(Timetable.class);
        when(mapper.mapToDomainEntity(e1)).thenReturn(d1);
        when(mapper.mapToDomainEntity(e2)).thenReturn(d2);

        List<Timetable> result = sut.findByYearSemesterAndLectureCodeIn(year, semester, campus, codes);

        assertThat(result).containsExactly(d1, d2);
        verify(repository).findByYearAndSemesterAndCampusAndLectureCodeIn(year, semester, campus.name(), codes);
        verify(mapper).mapToDomainEntity(e1);
        verify(mapper).mapToDomainEntity(e2);
    }

    @Test
    @DisplayName("findByYearSemesterAndLectureCodeNotIn: 제외 목록 기반 조회 → Mapper 매핑")
    void findByYearSemesterAndLectureCodeNotIn_maps() {
        int year = 2021; int semester = 1; List<String> codes = List.of("HIS100");

        TimetableJpaEntity e = mock(TimetableJpaEntity.class);
        when(repository.findByYearAndSemesterAndLectureCodeNotIn(year, semester, codes))
                .thenReturn(List.of(e));

        Timetable d = mock(Timetable.class);
        when(mapper.mapToDomainEntity(e)).thenReturn(d);

        List<Timetable> result = sut.findByYearSemesterAndLectureCodeNotIn(year, semester, codes);

        assertThat(result).containsExactly(d);
        verify(repository).findByYearAndSemesterAndLectureCodeNotIn(year, semester, codes);
        verify(mapper).mapToDomainEntity(e);
    }
}