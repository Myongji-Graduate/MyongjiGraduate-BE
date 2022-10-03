package com.plzgraduate.myongjigraduatebe.graduation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
  Optional<GraduationRequirement> findByDepartmentAndEntryYear(
      Department department,
      int entryYear
  );
}
