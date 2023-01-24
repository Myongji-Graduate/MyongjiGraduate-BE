package com.plzgraduate.myongjigraduatebe.graduation.dto;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class BachelorInfoRequirementResponse {

  private final int commonCultureCredit;
  private final int coreCultureCredit;
  private final int basicAcademicalCultureCredit;
  private final int normalCulture;
  private final int major;
  private final int freeElective;
  private final int totalCredit;

  public static BachelorInfoRequirementResponse from(GraduationRequirement entity) {
    return new BachelorInfoRequirementResponse(
        entity.getCommonCultureCredit(),
        entity.getCoreCultureCredit(),
        entity.getBasicAcademicalCultureCredit(),
        entity.getNormalCultureCredit(),
        entity.getMajorCredit(),
        entity.getFreeElectiveCredit(),
        entity.getTotalCredit()
    );
  }
}
