package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class StudentPageInfoResponse {

  private final String studentNumber;

  private final String studentName;

  private final String department;


  @Builder
  public StudentPageInfoResponse(
      String studentNumber,
      String studentName,
      String department
  ) {
    this.studentNumber = studentNumber;
    this.studentName = studentName;
    this.department = department;
  }
}
