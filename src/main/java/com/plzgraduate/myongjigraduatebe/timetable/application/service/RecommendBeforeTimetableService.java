package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.RecommendBeforeTimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.TimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.RecommendBeforeTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.Timetable;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.recommend.FreeDay;
import com.plzgraduate.myongjigraduatebe.user.application.port.FindUserPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@Transactional
@RequiredArgsConstructor
public class RecommendBeforeTimetableService implements RecommendBeforeTimetableUseCase {
    private final FindUserPort findUserPort;


    private final RequirementSnapshotQueryPort requirementSnapshotQueryPort;
    private final TakenLectureQuery takenLectureQuery;
    private final RemainingSemesterCalculator remainingSemesterCalculator;
    private final TimetablePort timetablePort;

    @Override
    public RecommendBeforeTimetableResponse recommend(Long userId, int targetCredits, List<FreeDay> freeDays, int year, int semester) {
        User user = findUserPort.findUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 1) 남은 학기 계산 (8 - completedSemesterCount, 최소 1)
        int remainingSemesters = remainingSemesterCalculator.from(user);

        // 2) 요구 스냅샷(버킷 타깃/채플 남음 포함)
        var req = requirementSnapshotQueryPort.getSnapshot(user, remainingSemesters);

        // 3) 이미 들은 과목 제외
        // Removed redundant declaration of taken

        // 4) 개설 강좌 풀에서 이수 과목 제외
        Set<String> takenCodes = takenLectureQuery.findAlreadyTakenLectureCodes(user);

        List<Timetable> pool = timetablePort.findByYearAndSemester(year, semester);
        List<Timetable> notTaken = pool.stream()
                .filter(tt -> {
                    // 채플 과목 예외 처리: 남은 회차가 있으면 제외하지 않는다
                    if (isChapel(tt)) {
                        var chapelItem = req.getItems().get(GraduationCategory.CHAPEL);
                        boolean chapelRemaining = chapelItem != null && chapelItem.getTakenCredit() < chapelItem.getTotalCredit();
                        return chapelRemaining;
                    }
                    // 그 외 과목은 일반 규칙: 이미 들은 과목이면 제외
                    return !takenCodes.contains(tt.getLectureCode());
                })
                .collect(Collectors.toList());

        //5) 공강 요일 제외
        // 사용자가 선택한 공강 요일
        Set<FreeDay> freeDaySet = Set.copyOf(freeDays);

        // day1 / day2 중 하나라도 공강 요일이면 제외
        List<Timetable> freeDayFiltered = notTaken.stream()
                .filter(tt -> !isOnFreeDay(tt, freeDaySet))
                .collect(Collectors.toList());

        // 채플 남은 경우: 최소 1개는 무조건 포함 (단, 공강 요일에 모두 있으면 생략)
        var chapelItem = req.getItems().get(GraduationCategory.CHAPEL);
        boolean chapelRemaining = chapelItem != null && chapelItem.getTakenCredit() < chapelItem.getTotalCredit();
        if (chapelRemaining) {
            boolean alreadyIncluded = freeDayFiltered.stream().anyMatch(this::isChapel);
            if (!alreadyIncluded) {
                // 공강 요일을 피하는 채플만 추가, 없으면 생략
                Timetable chapelPick = notTaken.stream()
                        .filter(this::isChapel)
                        .filter(tt -> !isOnFreeDay(tt, freeDaySet))
                        .findFirst()
                        .orElse(null);

                if (chapelPick != null) {
                    freeDayFiltered.add(chapelPick);
                }
            }
        }

        var lectures = freeDayFiltered.stream()
                .map(TimetableResponse::from)
                .collect(Collectors.toList());

        int total = lectures.stream().mapToInt(TimetableResponse::getCredit).sum();

        return RecommendBeforeTimetableResponse.builder()
                .totalCredits(total)
                .lectures(lectures)
                .build();
    }

    private boolean isOnFreeDay(Timetable tt, Set<FreeDay> freeDaySet) {
        return matchesFreeDay(tt.getDay1(), freeDaySet)
                || matchesFreeDay(tt.getDay2(), freeDaySet);
    }

    private boolean matchesFreeDay(String dayKorean, Set<FreeDay> freeDaySet) {
        if (dayKorean == null) return false;
        FreeDay mapped = mapKoreanDayToFreeDay(dayKorean);
        return mapped != null && freeDaySet.contains(mapped);
    }

    // TODO: 나머지 추천 로직 (전공/교양 균형, 빔서치 기반 시간표 최적화 등) 추후 구현 예정

    private FreeDay mapKoreanDayToFreeDay(String dayKorean) {
        switch (dayKorean) {
            case "월요일": return FreeDay.MON;
            case "화요일": return FreeDay.TUE;
            case "수요일": return FreeDay.WED;
            case "목요일": return FreeDay.THU;
            case "금요일": return FreeDay.FRI;
            case "토요일": return FreeDay.SAT;
            case "일요일": return FreeDay.SUN;
            default: return null; // 예외 케이스(집합 과목 등)면 null
        }
    }

    private boolean isChapel(Timetable tt) {
        String name = tt.getName();
        String note = tt.getNote();
        return (name != null && name.contains("채플"))
                || (note != null && note.contains("채플"));
    }
}
