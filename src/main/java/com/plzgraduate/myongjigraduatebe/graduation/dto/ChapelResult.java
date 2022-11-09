package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Getter;

@Getter
public class ChapelResult {

  private final boolean isCompleted;
  private final int totalCount = 4;
  private final int takenCount;

  public ChapelResult(
      int takenCount
  ) {
    this.takenCount = takenCount;
    this.isCompleted = totalCount <= takenCount;
  }
}
