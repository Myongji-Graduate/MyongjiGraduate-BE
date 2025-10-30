package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * 전체 8학기 기준으로, 이미 수료한 학기를 제외하고 남은 학기를 계산한다.
 * 성적표가 없는 경우 예외를 발생시킨다.
 */
@Component
public class RemainingSemesterCalculator {

    private static final int TOTAL_SEMESTERS = 8;

    /**
     * 남은 학기 수 계산
     * @param user 현재 로그인 사용자 (completedSemesterCount 기반)
     * @return 남은 학기 수 (최소 1)
     * @throws IllegalStateException 성적표가 없는 경우
     */
    public int from(User user) {
        Integer completed = user.getCompletedSemesterCount();

        if (completed == null) {
            // 성적표를 아직 등록하지 않은 사용자
            throw new IllegalStateException("성적표를 등록하거나 재등록 해주세요");
        }

        int remaining = TOTAL_SEMESTERS - completed;

        // 최소 1학기 보장
        return Math.max(1, remaining);
    }
}