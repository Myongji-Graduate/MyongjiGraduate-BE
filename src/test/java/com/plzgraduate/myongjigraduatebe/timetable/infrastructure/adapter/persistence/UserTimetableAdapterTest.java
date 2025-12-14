package com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence;

import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.TimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.entity.UserTimetableJpaEntity;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.TimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.mapper.UserTimetableMapper;
import com.plzgraduate.myongjigraduatebe.timetable.infrastructure.adapter.persistence.repository.UserTimetableRepository;
import com.plzgraduate.myongjigraduatebe.user.infrastructure.adapter.persistence.entity.UserJpaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTimetableAdapterTest{

    @Mock private UserTimetableRepository userTimetableRepository;
    @Mock private UserTimetableMapper userTimetableMapper;
    @Mock private TimetableMapper timetableMapper;
    @Mock private EntityManager em;

    @InjectMocks
    private UserTimetableAdapter adapter;

    @Test
    @DisplayName("deleteByUserAndSemester: repository 위임 호출")
    void deleteByUserAndSemester_delegates() {
        Long userId = 11L;
        int year = 2025;
        int semester = 2;

        adapter.deleteByUserAndSemester(userId, year, semester);

        verify(userTimetableRepository).deleteByUser_IdAndYearAndSemester(userId, year, semester);
        verifyNoMoreInteractions(userTimetableRepository, userTimetableMapper, timetableMapper, em);
    }

    @Test
    @DisplayName("saveAll: getReference(유저/시간표) → 매퍼로 JPA 변환 → saveAll 호출")
    void saveAll_mapsAndSaves() {
        Long userId = 7L;
        int year = 2024;
        int semester = 1;

        UserTimetable ut1 = UserTimetable.builder()
                .userId(userId).timetableId(100L).year(year).semester(semester).build();
        UserTimetable ut2 = UserTimetable.builder()
                .userId(userId).timetableId(200L).year(year).semester(semester).build();
        List<UserTimetable> toSave = List.of(ut1, ut2);

        UserJpaEntity userRef = mock(UserJpaEntity.class);
        TimetableJpaEntity ttRef1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity ttRef2 = mock(TimetableJpaEntity.class);

        when(em.getReference(UserJpaEntity.class, userId)).thenReturn(userRef);
        when(em.getReference(TimetableJpaEntity.class, 100L)).thenReturn(ttRef1);
        when(em.getReference(TimetableJpaEntity.class, 200L)).thenReturn(ttRef2);

        UserTimetableJpaEntity e1 = mock(UserTimetableJpaEntity.class);
        UserTimetableJpaEntity e2 = mock(UserTimetableJpaEntity.class);
        when(userTimetableMapper.mapToJpaEntity(ut1, userRef, ttRef1)).thenReturn(e1);
        when(userTimetableMapper.mapToJpaEntity(ut2, userRef, ttRef2)).thenReturn(e2);

        adapter.saveAll(toSave);

        // getReference 호출 검증 (유저 2회, 시간표 2회)
        verify(em, times(2)).getReference(UserJpaEntity.class, userId); // saveAll maps each item, so same userRef is requested per item (called twice here).
        verify(em).getReference(TimetableJpaEntity.class, 100L);
        verify(em).getReference(TimetableJpaEntity.class, 200L);

        // 매핑 검증
        verify(userTimetableMapper).mapToJpaEntity(ut1, userRef, ttRef1);
        verify(userTimetableMapper).mapToJpaEntity(ut2, userRef, ttRef2);

        // 저장 검증 (캡처해서 e1, e2가 들어갔는지 확인)
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserTimetableJpaEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(userTimetableRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).containsExactlyInAnyOrder(e1, e2);

        verifyNoMoreInteractions(userTimetableRepository, userTimetableMapper, timetableMapper, em);
    }

    @Test
    @DisplayName("findTimetablesByUserAndSemester: repository → mapper 변환 후 도메인 Timetable 리스트 반환")
    void findTimetablesByUserAndSemester_mapsToDomain() {
        Long userId = 3L;
        int year = 2023;
        int semester = 2;

        TimetableJpaEntity j1 = mock(TimetableJpaEntity.class);
        TimetableJpaEntity j2 = mock(TimetableJpaEntity.class);
        when(userTimetableRepository.findTimetablesOfUser(userId, year, semester))
                .thenReturn(List.of(j1, j2));

        Timetable d1 = mock(Timetable.class);
        Timetable d2 = mock(Timetable.class);
        when(timetableMapper.mapToDomainEntity(j1)).thenReturn(d1);
        when(timetableMapper.mapToDomainEntity(j2)).thenReturn(d2);

        List<Timetable> result = adapter.findTimetablesByUserAndSemester(userId, year, semester);

        assertThat(result).containsExactly(d1, d2);
        verify(userTimetableRepository).findTimetablesOfUser(userId, year, semester);
        verify(timetableMapper).mapToDomainEntity(j1);
        verify(timetableMapper).mapToDomainEntity(j2);
        verifyNoMoreInteractions(userTimetableRepository, userTimetableMapper, timetableMapper, em);
    }
}