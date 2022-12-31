package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.Getter;

@Getter
public class StudentFindIdResponse {

  private final String userId;

  private final String studentNumber;

  public StudentFindIdResponse(
      String userId,
      String studentNumber
  ) {
    this.userId = userId;
    this.studentNumber = studentNumber;
  }
}
