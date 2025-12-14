package com.plzgraduate.myongjigraduatebe.timetable.application.service;

import com.plzgraduate.myongjigraduatebe.completedcredit.application.port.FindCompletedCreditPort;
import com.plzgraduate.myongjigraduatebe.completedcredit.domain.model.CompletedCredit;
import com.plzgraduate.myongjigraduatebe.graduation.domain.model.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.timetable.application.port.RequirementSnapshotQueryPort;
import com.plzgraduate.myongjigraduatebe.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
class RequirementSnapshotQueryImpl implements RequirementSnapshotQueryPort {

    private final FindCompletedCreditPort findCompletedCreditPort;

    @Override
    public RequirementSnapshot getSnapshot(User user, int remainingSemesters) {
        List<CompletedCredit> credits = findCompletedCreditPort.findCompletedCredit(user);

        Map<GraduationCategory, RequirementSnapshot.Item> map = new EnumMap<>(GraduationCategory.class);
        for (CompletedCredit credit : credits) {
            map.put(
                    credit.getGraduationCategory(),
                    RequirementSnapshot.Item.builder()
                            .category(credit.getGraduationCategory())
                            .totalCredit(credit.getTotalCredit())
                            .takenCredit(credit.getTakenCredit())
                            .build()
            );
        }

        return RequirementSnapshot.builder()
                .items(map)
                .build();
    }
}