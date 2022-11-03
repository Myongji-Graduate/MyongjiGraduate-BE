package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureDto {
  private final String code;
  private final String name;
  private final int credit;

  @Builder
  public TakenLectureDto(
      String code,
      String name,
      int credit
  ) {
    this.code = code;
    this.name = name;
    this.credit = credit;
  }
}
