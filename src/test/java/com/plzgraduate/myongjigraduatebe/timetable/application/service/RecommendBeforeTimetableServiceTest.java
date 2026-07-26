package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendBeforeTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendBeforeTimetableServiceTest {

    @Mock
    private FindUserPort findUserPort;
    @Mock
    private RequirementSnapshotQueryPort requirementSnapshotQueryPort;
    @Mock
    private TakenLectureQuery takenLectureQuery;
    @Mock
    private RemainingSemesterCalculator remainingSemesterCalculator;
    @Mock
    private TimetablePort timetablePort;
    @Mock
    private FusionMajorMembershipPort fusionMajorMembershipPort;

    @InjectMocks
    private RecommendBeforeTimetableService sut;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .completedSemesterCount(4)
                .build();

        when(findUserPort.findUserById(1L)).thenReturn(Optional.of(user));
        when(remainingSemesterCalculator.from(user)).thenReturn(4);
        when(requirementSnapshotQueryPort.getSnapshot(user, 4))
                .thenReturn(RequirementSnapshot.builder().build());
        when(takenLectureQuery.findAlreadyTakenLectureCodes(user)).thenReturn(Set.of());
    }

    @Test
    @DisplayName("목표 학점을 초과하지 않으면서 가장 가까운 조합을 반환한다")
    void respectsTargetCredits() {
        when(timetablePort.findByYearAndSemester(2026, 1)).thenReturn(List.of(
                timetable(1L, "A", "0001", 3, "월요일", 540, 600),
                timetable(2L, "B", "0001", 3, "화요일", 540, 600),
                timetable(3L, "C", "0001", 2, "수요일", 540, 600)
        ));

        RecommendBeforeTimetableResponse result = sut.recommend(1L, 5, List.of(), 2026, 1);

        assertThat(result.getTotalCredits()).isEqualTo(5);
        assertThat(result.getLectures()).extracting("lectureCode")
                .containsExactlyInAnyOrder("A", "C");
    }

    @Test
    @DisplayName("수업 시간이 겹치는 강의는 같은 시간표에 포함하지 않는다")
    void excludesTimeConflicts() {
        when(timetablePort.findByYearAndSemester(2026, 1)).thenReturn(List.of(
                timetable(1L, "A", "0001", 3, "월요일", 540, 600),
                timetable(2L, "B", "0001", 3, "월요일", 570, 630),
                timetable(3L, "C", "0001", 3, "화요일", 540, 600)
        ));

        RecommendBeforeTimetableResponse result = sut.recommend(1L, 6, List.of(), 2026, 1);

        assertThat(result.getLectures()).extracting("lectureCode")
                .containsExactlyInAnyOrder("A", "C")
                .doesNotContain("B");
    }

    @Test
    @DisplayName("동일 과목의 여러 분반은 하나만 선택한다")
    void selectsOnlyOneDivisionPerLecture() {
        when(timetablePort.findByYearAndSemester(2026, 1)).thenReturn(List.of(
                timetable(1L, "A", "0001", 3, "월요일", 540, 600),
                timetable(2L, "A", "0002", 3, "화요일", 540, 600),
                timetable(3L, "B", "0001", 3, "수요일", 540, 600)
        ));

        RecommendBeforeTimetableResponse result = sut.recommend(1L, 6, List.of(), 2026, 1);

        assertThat(result.getLectures()).extracting("lectureCode")
                .containsExactlyInAnyOrder("A", "B");
    }

    @Test
    @DisplayName("공강 요일 목록이 null이어도 빈 조건으로 처리한다")
    void handlesNullFreeDays() {
        when(timetablePort.findByYearAndSemester(2026, 1)).thenReturn(List.of(
                timetable(1L, "A", "0001", 3, "월요일", 540, 600)
        ));

        RecommendBeforeTimetableResponse result = sut.recommend(1L, 3, null, 2026, 1);

        assertThat(result.getTotalCredits()).isEqualTo(3);
    }

    private Timetable timetable(
            Long id,
            String lectureCode,
            String division,
            int credit,
            String day,
            int start,
            int end
    ) {
        return Timetable.builder()
                .id(id)
                .year(2026)
                .semester(1)
                .lectureCode(lectureCode)
                .classDivision(division)
                .name("강의 " + lectureCode)
                .credit(credit)
                .day1(day)
                .startMinute1(start)
                .endMinute1(end)
                .build();
    }
}
