package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResult {

  private final boolean isCompleted;
  private final int totalCredit;
  private final int takenCredit;
  private final List<DetailCategoryResult> detailCategory;

  @Builder
  private DetailGraduationResult(
      int totalCredit,
      int takenCredit,
      List<DetailCategoryResult> detailCategory
  ) {
    this.totalCredit = totalCredit;
    this.takenCredit = takenCredit;
    this.detailCategory = detailCategory;
    this.isCompleted = checkComplete();
  }

  private boolean checkComplete() {
    boolean isCompleted = true;

    for (DetailCategoryResult categoryResult : detailCategory) {
      isCompleted = isCompleted && categoryResult.isCompleted();
    }

    return isCompleted && totalCredit <= takenCredit;
  }
}
