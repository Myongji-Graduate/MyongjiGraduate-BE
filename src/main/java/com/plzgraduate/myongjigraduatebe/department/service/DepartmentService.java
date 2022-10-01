package com.plzgraduate.myongjigraduatebe.department.service;

import com.plzgraduate.myongjigraduatebe.department.service.dto.AllDepartmentsResponse;

public interface DepartmentService {

  AllDepartmentsResponse findAll();

  boolean isSupportedEntryYear(long id, int entryYear);
}
