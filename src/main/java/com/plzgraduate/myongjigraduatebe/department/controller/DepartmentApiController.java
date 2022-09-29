package com.plzgraduate.myongjigraduatebe.department.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.department.service.DepartmentService;
import com.plzgraduate.myongjigraduatebe.department.service.dto.AllDepartmentsResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/departments")
public class DepartmentApiController {

  private final DepartmentService departmentService;

  @GetMapping()
  @ResponseStatus(HttpStatus.OK)
  public AllDepartmentsResponse findAll() {
    return departmentService.findAll();
  }

}
