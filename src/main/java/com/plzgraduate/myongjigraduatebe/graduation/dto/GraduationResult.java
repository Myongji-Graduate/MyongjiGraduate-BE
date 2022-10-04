package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationResult {

  private final BasicInfo basicInfo;
  private final boolean isGraduated;
  private final ChapelResult chapelResult;
  private final DetailGraduationResult commonCulture;
  private final DetailGraduationResult coreCulture;
  private final DetailGraduationResult basicAcademicalCulture;
  private final DetailGraduationResult major;
  private final DetailGraduationResult normalCulture;
  private final DetailGraduationResult freeElective;

  @Builder
  private GraduationResult(
      BasicInfo basicInfo,
      boolean isGraduated,
      ChapelResult chapelResult,
      DetailGraduationResult commonCulture,
      DetailGraduationResult coreCulture,
      DetailGraduationResult basicAcademicalCulture,
      DetailGraduationResult major,
      DetailGraduationResult normalCulture,
      DetailGraduationResult freeElective
  ) {
    this.basicInfo = basicInfo;
    this.isGraduated = isGraduated;
    this.chapelResult = chapelResult;
    this.commonCulture = commonCulture;
    this.coreCulture = coreCulture;
    this.basicAcademicalCulture = basicAcademicalCulture;
    this.major = major;
    this.normalCulture = normalCulture;
    this.freeElective = freeElective;
  }
}
