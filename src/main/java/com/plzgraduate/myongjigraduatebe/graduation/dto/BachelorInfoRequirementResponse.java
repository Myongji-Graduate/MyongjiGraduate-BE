package com.plzgraduate.myongjigraduatebe.graduation.dto;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BachelorInfoRequirementResponse {

  public static final int CHAPEL_CREDIT = 2;

  private final int commonCultureCredit;
  private final int coreCultureCredit;
  private final int basicAcademicalCultureCredit;
  private final int normalCultureCredit;
  private final int majorCredit;
  private final int freeElectiveCredit;
  private final int totalCredit;

  public static BachelorInfoRequirementResponse from(GraduationRequirement entity) {
    return new BachelorInfoRequirementResponse(
        entity.getCommonCultureCredit() + CHAPEL_CREDIT,
        entity.getCoreCultureCredit(),
        entity.getBasicAcademicalCultureCredit(),
        entity.getNormalCultureCredit(),
        entity.getMajorCredit(),
        entity.getFreeElectiveCredit(),
        entity.getTotalCredit() + CHAPEL_CREDIT
    );
  }
}
