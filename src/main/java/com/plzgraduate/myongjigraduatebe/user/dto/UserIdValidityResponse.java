package com.plzgraduate.myongjigraduatebe.user.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonIgnoreProperties(value = "duplicated")
public class UserIdValidityResponse {

  private final UserId userId;

  private final boolean duplicated;

  @JsonGetter()
  public boolean getIsNotDuplicated() {
    return !duplicated;
  }
}
