package com.plzgraduate.myongjigraduatebe.user.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(value = "duplicated")
public class StudentNumberValidityResponse {

  private final StudentNumber studentNumber;

  private final boolean duplicated;

  @JsonGetter()
  public boolean getIsNotDuplicated() {
    return !duplicated;
  }
}
