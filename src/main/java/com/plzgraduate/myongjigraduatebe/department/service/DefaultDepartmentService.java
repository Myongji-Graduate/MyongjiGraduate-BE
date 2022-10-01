package com.plzgraduate.myongjigraduatebe.department.service;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.department.service.dto.AllDepartmentsResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class DefaultDepartmentService implements DepartmentService {

  private final DepartmentRepository departmentRepository;

  @Override
  public AllDepartmentsResponse findAll() {
    return AllDepartmentsResponse.from(departmentRepository.findAll());
  }

  @Override
  public boolean isSupportedEntryYear(
      long id,
      int entryYear
  ) {
    Department department = departmentRepository
        .findById(id)
        .orElseThrow(() -> new IllegalArgumentException("학과를 찾을 수 없습니다."));

    return department.isSupported(entryYear);
  }
}
