package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.Collection;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResult {

  private final String categoryName;
  private boolean isCompleted;
  private final int totalCredit;
  private int takenCredit;
  private final Collection<DetailCategoryResult> detailCategory;

  @Builder
  private DetailGraduationResult(
      String categoryName,
      int totalCredit,
      int takenCredit,
      Collection<DetailCategoryResult> detailCategory
  ) {
    this.categoryName = categoryName;
    this.totalCredit = totalCredit;
    this.takenCredit = takenCredit;
    this.detailCategory = detailCategory;
  }

  public static DetailGraduationResult createFreeElective(GraduationRequirement requirement) {
    return new DetailGraduationResult(GraduationCategory.FREE_ELECTIVE.name(), requirement.getFreeElectiveCredit(), 0, null);
  }

  public static DetailGraduationResult createNormalCulture(GraduationRequirement requirement) {
    return new DetailGraduationResult(GraduationCategory.NORMAL_CULTURE.name(), requirement.getNormalCultureCredit(), 0, null);
  }

  private boolean checkComplete() {
    boolean isCompleted = true;

    for (DetailCategoryResult categoryResult : detailCategory) {
      isCompleted = isCompleted && categoryResult.isCompleted();
    }

    return isCompleted && totalCredit <= takenCredit;
  }

  public int updateTakenCredit() {
    this.isCompleted = checkComplete();

    int leftCredit = 0;

    if (totalCredit < takenCredit) {
      leftCredit = takenCredit - totalCredit;
      takenCredit = totalCredit;
    }

    return leftCredit;
  }

  public void addTakenCredit(int takenCredit) {
    this.takenCredit += takenCredit;
    this.isCompleted = checkComplete();
  }
}
