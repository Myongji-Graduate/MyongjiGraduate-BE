package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Getter;

@Getter
public class ChapelResult {

  private final boolean isCompleted;
  private final int totalCount;
  private final int takenCount;

  public ChapelResult(
      int totalCount,
      int takenCount
  ) {
    this.totalCount = totalCount;
    this.takenCount = takenCount;
    this.isCompleted = totalCount <= takenCount;
  }
}
