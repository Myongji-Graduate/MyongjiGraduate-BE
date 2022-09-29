package com.plzgraduate.myongjigraduatebe.department.service.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AllDepartmentsResponse {

  private final List<DepartmentResponse> departments;

  public static AllDepartmentsResponse from(List<Department> departments) {
    return new AllDepartmentsResponse(departments
                                          .stream()
                                          .map(DepartmentResponse::from)
                                          .collect(Collectors.toList()));
  }

}
