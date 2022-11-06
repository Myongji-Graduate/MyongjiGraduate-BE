package com.plzgraduate.myongjigraduatebe.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

import lombok.Getter;

@Getter
public class SignInRequest {

  private final UserId userId;
  private final Password password;

  @JsonCreator
  public SignInRequest(
      @JsonProperty("userId")
      UserId userId,
      @JsonProperty("password")
      Password password
  ) {
    this.userId = userId;
    this.password = password;
  }
}
