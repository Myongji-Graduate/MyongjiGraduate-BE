package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.lecture.application.port.FusionMajorMembershipPort;
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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@UseCase
@Transactional
@RequiredArgsConstructor
public class RecommendBeforeTimetableService implements RecommendBeforeTimetableUseCase {
    private static final int BEAM_WIDTH = 200;

    private final FindUserPort findUserPort;


    private final RequirementSnapshotQueryPort requirementSnapshotQueryPort;
    private final TakenLectureQuery takenLectureQuery;
    private final RemainingSemesterCalculator remainingSemesterCalculator;
    private final TimetablePort timetablePort;
    private final FusionMajorMembershipPort fusionMajorMembershipPort;

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
        List<String> poolCodes = pool.stream().map(Timetable::getLectureCode)
                .filter(Objects::nonNull).toList();
        Set<String> fusionOnlyCodes = fusionMajorMembershipPort.findFusionMajorLectureIds(poolCodes);
        Set<String> allowedFusionCodes = fusionMajorMembershipPort
                .findLectures(user.getAssociatedMajor(), user.getEntryYear()).stream()
                .map(lecture -> lecture.getId()).collect(Collectors.toSet());
        List<Timetable> notTaken = pool.stream()
                .filter(tt -> !fusionOnlyCodes.contains(tt.getLectureCode())
                        || allowedFusionCodes.contains(tt.getLectureCode()))
                .filter(tt -> {
                    // 채플 과목 예외 처리: 남은 회차가 있으면 제외하지 않는다
                    if (isChapel(tt)) {
                        var chapelItem = req.getItems().get(GraduationCategory.CHAPEL);
                        return chapelItem != null && chapelItem.getTakenCredit() < chapelItem.getTotalCredit();
                    }
                    // 그 외 과목은 일반 규칙: 이미 들은 과목이면 제외
                    return !takenCodes.contains(tt.getLectureCode());
                })
                .collect(Collectors.toList());

        // 5) 공강 요일 제외
        Set<FreeDay> freeDaySet = freeDays == null ? Set.of() : Set.copyOf(freeDays);

        List<Timetable> candidates = notTaken.stream()
                .filter(tt -> !isOnFreeDay(tt, freeDaySet))
                .filter(tt -> tt.getCredit() >= 0)
                .sorted(Comparator
                        .comparing((Timetable tt) -> !isChapel(tt))
                        .thenComparing(Timetable::getLectureCode, Comparator.nullsLast(String::compareTo))
                        .thenComparing(Timetable::getClassDivision, Comparator.nullsLast(String::compareTo))
                        .thenComparing(Timetable::getId, Comparator.nullsLast(Long::compareTo)))
                .collect(Collectors.toList());

        var chapelItem = req.getItems().get(GraduationCategory.CHAPEL);
        boolean chapelRemaining = chapelItem != null && chapelItem.getTakenCredit() < chapelItem.getTotalCredit();
        List<Timetable> selected = selectBestCombination(candidates, targetCredits, chapelRemaining);

        var lectures = selected.stream()
                .map(TimetableResponse::from)
                .collect(Collectors.toList());

        int total = lectures.stream().mapToInt(TimetableResponse::getCredit).sum();

        return RecommendBeforeTimetableResponse.builder()
                .totalCredits(total)
                .lectures(lectures)
                .build();
    }

    private List<Timetable> selectBestCombination(
            List<Timetable> candidates,
            int targetCredits,
            boolean chapelRemaining
    ) {
        List<SelectionState> beam = new ArrayList<>();
        beam.add(SelectionState.empty());

        for (Timetable candidate : candidates) {
            List<SelectionState> next = new ArrayList<>(beam);
            for (SelectionState state : beam) {
                if (state.canAdd(candidate, targetCredits)) {
                    next.add(state.add(candidate, isChapel(candidate)));
                }
            }

            next.sort(Comparator
                    .comparing((SelectionState state) -> chapelRemaining && !state.hasChapel())
                    .thenComparingInt(state -> targetCredits - state.totalCredits())
                    .thenComparingInt(state -> state.lectures().size()));

            beam = next.stream()
                    .distinct()
                    .limit(BEAM_WIDTH)
                    .collect(Collectors.toCollection(ArrayList::new));
        }

        return beam.getFirst().lectures();
    }

    private record SelectionState(
            List<Timetable> lectures,
            Set<String> lectureCodes,
            int totalCredits,
            boolean hasChapel
    ) {
        static SelectionState empty() {
            return new SelectionState(List.of(), Set.of(), 0, false);
        }

        boolean canAdd(Timetable candidate, int targetCredits) {
            if (candidate.getLectureCode() == null || lectureCodes.contains(candidate.getLectureCode())) {
                return false;
            }
            if (totalCredits + candidate.getCredit() > targetCredits) {
                return false;
            }
            return lectures.stream().noneMatch(selected -> conflicts(selected, candidate));
        }

        SelectionState add(Timetable candidate, boolean chapel) {
            List<Timetable> nextLectures = new ArrayList<>(lectures);
            nextLectures.add(candidate);
            Set<String> nextCodes = new HashSet<>(lectureCodes);
            nextCodes.add(candidate.getLectureCode());
            return new SelectionState(
                    List.copyOf(nextLectures),
                    Set.copyOf(nextCodes),
                    totalCredits + candidate.getCredit(),
                    hasChapel || chapel
            );
        }

        private static boolean conflicts(Timetable left, Timetable right) {
            return conflicts(left.getDay1(), left.getStartMinute1(), left.getEndMinute1(),
                    right.getDay1(), right.getStartMinute1(), right.getEndMinute1())
                    || conflicts(left.getDay1(), left.getStartMinute1(), left.getEndMinute1(),
                    right.getDay2(), right.getStartMinute2(), right.getEndMinute2())
                    || conflicts(left.getDay2(), left.getStartMinute2(), left.getEndMinute2(),
                    right.getDay1(), right.getStartMinute1(), right.getEndMinute1())
                    || conflicts(left.getDay2(), left.getStartMinute2(), left.getEndMinute2(),
                    right.getDay2(), right.getStartMinute2(), right.getEndMinute2());
        }

        private static boolean conflicts(
                String leftDay,
                Integer leftStart,
                Integer leftEnd,
                String rightDay,
                Integer rightStart,
                Integer rightEnd
        ) {
            if (!Objects.equals(leftDay, rightDay) || leftDay == null) {
                return false;
            }
            if (leftStart == null || leftEnd == null || rightStart == null || rightEnd == null) {
                return false;
            }
            return leftStart < rightEnd && rightStart < leftEnd;
        }
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
