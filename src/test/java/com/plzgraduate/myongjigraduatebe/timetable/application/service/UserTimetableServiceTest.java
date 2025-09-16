package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.UserTimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserTimetableServiceTest {

    @Mock
    private UserTimetablePort userTimetablePort;

    @InjectMocks
    private UserTimetableService sut;

    @Test
    @DisplayName("replaceLectures: 기존 슬롯 비우고, 중복 제거 후 saveAll 호출")
    void replaceLectures_removesDuplicates_andSaves() {
        Long userId = 1L;
        int year = 2025;
        int semester = 2;
        List<Long> input = List.of(10L, 10L, 20L); // 중복 포함

        sut.replaceLectures(userId, year, semester, input);

        // 1) 먼저 해당 연/학기 데이터 삭제
        verify(userTimetablePort).deleteByUserAndSemester(userId, year, semester);

        // 2) 저장 파라미터 캡처해서 중복 제거되었는지 확인
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<UserTimetable>> captor = ArgumentCaptor.forClass(List.class);
        verify(userTimetablePort).saveAll(captor.capture());
        List<UserTimetable> saved = captor.getValue();

        assertThat(saved).hasSize(2);
        assertThat(saved).extracting(UserTimetable::getTimetableId).containsExactlyInAnyOrder(10L, 20L);
        assertThat(saved).allSatisfy(u -> {
            assertThat(u.getUserId()).isEqualTo(userId);
            assertThat(u.getYear()).isEqualTo(year);
            assertThat(u.getSemester()).isEqualTo(semester);
        });
    }

    @Test
    @DisplayName("getMyLectures: Port 결과를 TimetableResponse.from 으로 매핑")
    void getMyLectures_mapsResponses() {
        Long userId = 1L;
        int year = 2025;
        int semester = 1;

        Timetable t1 = mock(Timetable.class);
        Timetable t2 = mock(Timetable.class);

        List<Timetable> timetables = List.of(t1, t2);
        when(userTimetablePort.findTimetablesByUserAndSemester(userId, year, semester))
                .thenReturn(timetables);

        TimetableResponse r1 = mock(TimetableResponse.class);
        TimetableResponse r2 = mock(TimetableResponse.class);

        try (MockedStatic<TimetableResponse> mocked = mockStatic(TimetableResponse.class)) {
            mocked.when(() -> TimetableResponse.from(t1)).thenReturn(r1);
            mocked.when(() -> TimetableResponse.from(t2)).thenReturn(r2);

            List<TimetableResponse> result = sut.getMyLectures(userId, year, semester);

            assertThat(result).containsExactly(r1, r2);
            verify(userTimetablePort).findTimetablesByUserAndSemester(userId, year, semester);
            mocked.verify(() -> TimetableResponse.from(t1));
            mocked.verify(() -> TimetableResponse.from(t2));
        }
    }

    @Test
    @DisplayName("clearLectures: deleteByUserAndSemester 호출")
    void clearLectures_deletes() {
        sut.clearLectures(99L, 2024, 2);
        verify(userTimetablePort).deleteByUserAndSemester(99L, 2024, 2);
    }
}