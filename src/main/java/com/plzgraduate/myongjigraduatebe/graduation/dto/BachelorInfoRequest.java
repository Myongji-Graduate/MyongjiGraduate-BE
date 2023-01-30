package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Data;

@Data
public class BachelorInfoRequest {

  private final int entryYear;
  private final String department;

  public BachelorInfoRequest(

      int entryYear,
      String department
  ) {
    this.entryYear = entryYear;
    this.department = department;
  }
}
