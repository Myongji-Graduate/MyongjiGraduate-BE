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

  public boolean checkComplete() {
    boolean isCompleted = totalCredit <= takenCredit;

    if (detailCategory == null) {
      return isCompleted;
    }

    return isCompleted && checkDetailCategoryComplete();
  }

  private boolean checkDetailCategoryComplete() {
    boolean isCompleted = true;
    for (DetailCategoryResult categoryResult : detailCategory) {
      isCompleted = isCompleted && categoryResult.isCompleted();
    }
    return isCompleted;
  }

  public int getLeftCredit() {
    int leftCredit = 0;

    if (detailCategory == null) {
      leftCredit = Math.max(0, takenCredit - totalCredit);
    } else {
      for (DetailCategoryResult result : detailCategory) {
        leftCredit += result.getLeftCredit();
      }
    }

    takenCredit -= leftCredit;
    return leftCredit;
  }

  public void addTakenCredit(int takenCredit) {
    this.takenCredit += takenCredit;
    this.isCompleted = checkComplete();
  }
}
