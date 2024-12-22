package com.plzgraduate.myongjigraduatebe.graduation.domain.model;

import java.util.Arrays;
import java.util.NoSuchElementException;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransferGraduationRequirementType {
    HUMANITIES("인문대", 51, 2),
    SOCIAL_SCIENCE("사회과학대", 51, 2),
    BUSINESS("경영대", 45, 2),
    LAW("법과대", 48, 2),
    ICT("ICT융합대", 57, 2);

    private final String collegeName;
    private final int combinedCultureCredit;
    private final int christianCredit;

    public static TransferGraduationRequirementType findByCollegeName(String collegeName) {
        return Arrays.stream(TransferGraduationRequirementType.values())
                .filter(type -> type.getCollegeName().equals(collegeName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 단과대의 편입 졸업 요건이 존재하지 않습니다."));
    }
}
