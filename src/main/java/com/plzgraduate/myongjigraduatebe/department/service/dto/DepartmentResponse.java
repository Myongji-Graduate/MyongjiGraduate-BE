package com.plzgraduate.myongjigraduatebe.department.service.dto;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DepartmentResponse {

  private final long id;

  private final String name;

  public static DepartmentResponse from(Department entity) {
    return new DepartmentResponse(entity.getId(), entity.getName());
  }
}
