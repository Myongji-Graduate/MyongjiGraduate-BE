package com.plzgraduate.myongjigraduatebe.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
