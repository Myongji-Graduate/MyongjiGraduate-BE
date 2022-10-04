package com.plzgraduate.myongjigraduatebe.graduation.dto;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.Builder;
import lombok.Getter;

@Getter
public class BasicInfo {

  private final String name;
  private final String studentNumber;
  private final String department;
  private final int totalCredit;
  private final int takenCredit;

  @Builder
  private BasicInfo(
      String name,
      String studentNumber,
      Department department,
      int totalCredit,
      int takenCredit
  ) {
    this.name = name;
    this.studentNumber = studentNumber;
    this.department = department.getName();
    this.totalCredit = totalCredit;
    this.takenCredit = takenCredit;
  }
}
