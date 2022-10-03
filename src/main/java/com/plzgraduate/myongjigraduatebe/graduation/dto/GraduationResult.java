package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class GraduationResult {
  private final int entryYear;
  private final String department;
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
      int entryYear,
      String department,
      boolean isGraduated,
      ChapelResult chapelResult,
      DetailGraduationResult commonCulture,
      DetailGraduationResult coreCulture,
      DetailGraduationResult basicAcademicalCulture,
      DetailGraduationResult major,
      DetailGraduationResult normalCulture,
      DetailGraduationResult freeElective
  ) {
    this.entryYear = entryYear;
    this.department = department;
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
