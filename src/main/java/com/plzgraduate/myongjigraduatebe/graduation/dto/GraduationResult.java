package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Getter;

@Getter
public class GraduationResult {

  private final BasicInfo basicInfo;
  private boolean isGraduated;
  private final ChapelResult chapelResult;
  private DetailGraduationResult commonCulture;
  private DetailGraduationResult coreCulture;
  private DetailGraduationResult basicAcademicalCulture;
  private DetailGraduationResult major;
  private DetailGraduationResult normalCulture;
  private DetailGraduationResult freeElective;

  public GraduationResult(
      BasicInfo basicInfo,
      ChapelResult chapelResult
  ) {
    this.basicInfo = basicInfo;
    this.chapelResult = chapelResult;
  }

  public void setCommonCultureResult(DetailGraduationResult result) {
    this.commonCulture = result;
  }

  public void setCoreCultureResult(DetailGraduationResult result) {
    this.coreCulture = result;
  }

  public void setBasicAcademicalCultureResult(DetailGraduationResult result) {
    this.basicAcademicalCulture = result;
  }

  public void setMajorResult(DetailGraduationResult result) {
    this.major = result;
  }

  public void setNormalResult(DetailGraduationResult result) {
    this.normalCulture = result;
  }

  public void setFreeElectiveResult(DetailGraduationResult result) {
    this.freeElective = result;
  }

  public void checkGraduation() {
    updateLeftCredit();
    checkAllCompete();

    this.isGraduated = checkGraduate();
  }

  private boolean checkGraduate() {
    return commonCulture.isCompleted() &&
        coreCulture.isCompleted() &&
        basicAcademicalCulture.isCompleted() &&
        major.isCompleted() &&
        normalCulture.isCompleted() &&
        freeElective.isCompleted();
  }

  private void checkAllCompete() {
    commonCulture.checkComplete();
    coreCulture.checkComplete();
    basicAcademicalCulture.checkComplete();
    major.checkComplete();
    normalCulture.checkComplete();
    freeElective.checkComplete();
  }

  private void updateLeftCredit() {
    int leftCultureCredit =
        commonCulture.getLeftCredit() + coreCulture.getLeftCredit() + basicAcademicalCulture.getLeftCredit();
    normalCulture.addTakenCredit(leftCultureCredit);

    int leftFreeCredit = major.getLeftCredit() + normalCulture.getLeftCredit();
    freeElective.addTakenCredit(leftFreeCredit);
  }
}
