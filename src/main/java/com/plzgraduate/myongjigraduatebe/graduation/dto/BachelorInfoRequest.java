package com.plzgraduate.myongjigraduatebe.graduation.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class BachelorInfoRequest {

  private final int entryYear;
  private final String department;

  @JsonCreator
  public BachelorInfoRequest(
      @JsonProperty("entryYear")
      int entryYear,
      @JsonProperty("department")
      String department
  ) {
    this.entryYear = entryYear;
    this.department = department;
  }
}
