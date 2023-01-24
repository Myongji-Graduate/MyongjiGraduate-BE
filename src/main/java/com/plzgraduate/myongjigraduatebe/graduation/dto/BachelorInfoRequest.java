package com.plzgraduate.myongjigraduatebe.graduation.dto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;

import lombok.Getter;

@Getter
public class BachelorInfoRequest {

  private final EntryYear entryYear;
  private final String department;

  @JsonCreator
  public BachelorInfoRequest(
      @JsonProperty("entryYear")
      EntryYear entryYear,
      @JsonProperty("department")
      String department
  ) {
    this.entryYear = entryYear;
    this.department = department;
  }
}
