package com.plzgraduate.myongjigraduatebe.department.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
  Optional<Department> findByName(String departmentName);
}
