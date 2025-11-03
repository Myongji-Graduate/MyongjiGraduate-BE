package com.plzgraduate.myongjigraduatebe.timetable.application.port;

import com.plzgraduate.myongjigraduatebe.timetable.application.service.RequirementSnapshot;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;

public interface RequirementSnapshotQueryPort {

    /**
     * 주어진 사용자(User)와 남은 학기 수를 기준으로
     * 현재 남은 졸업 요건을 스냅샷 형태로 반환한다.
     *
     * @param user 사용자 도메인 객체
     * @param remainingSemesters 남은 학기 수 (예: 8 - completedSemesterCount)
     * @return RequirementSnapshot (전공/교양/자유선택/채플 등 버킷별 잔여 요건 포함)
     */
    RequirementSnapshot getSnapshot(User user, int remainingSemesters);
}
