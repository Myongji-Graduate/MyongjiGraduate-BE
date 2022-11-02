package com.plzgraduate.myongjigraduatebe.department.service;

import org.springframework.stereotype.Service;

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

}
