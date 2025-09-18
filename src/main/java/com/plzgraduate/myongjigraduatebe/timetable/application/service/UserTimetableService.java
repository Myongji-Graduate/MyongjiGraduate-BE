package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.core.meta.UseCase;
import com.plzgraduate.myongjigraduatebe.timetable.api.dto.response.TimetableResponse;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.UserTimetablePort;
import com.plzgraduate.myongjigraduatebe.timetable.application.usecase.UserTimetableUseCase;
import com.plzgraduate.myongjigraduatebe.timetable.domain.model.UserTimetable;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@UseCase
@RequiredArgsConstructor
public class UserTimetableService implements UserTimetableUseCase {


    private final UserTimetablePort userTimetablePort;

    @Override
    @Transactional
    public void replaceLectures(Long userId, int year, int semester, List<Long> timetableIds) {
        // 1) 기존 연/학기 슬롯 비우기
        userTimetablePort.deleteByUserAndSemester(userId, year, semester);

        // 2) 새로 세팅
        List<UserTimetable> toSave = timetableIds.stream()
                .distinct() // 중복 방지
                .map(tid -> UserTimetable.builder()
                        .userId(userId)
                        .timetableId(tid)
                        .year(year)
                        .semester(semester)
                        .build())
                .collect(Collectors.toList());

        userTimetablePort.saveAll(toSave);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TimetableResponse> getMyLectures(Long userId, int year, int semester) {
        return userTimetablePort.findTimetablesByUserAndSemester(userId, year, semester)
                .stream()
                .map(TimetableResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void clearLectures(Long userId, int year, int semester) {
        userTimetablePort.deleteByUserAndSemester(userId, year, semester);
    }
}
