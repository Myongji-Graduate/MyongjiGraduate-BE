package com.plzgraduate.myongjigraduatebe.graduation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {

  @Query(value = "select r from GraduationRequirement r join fetch r.department d where r.entryYear = :entryYear and r.engLv = :engLv and d.name = :departmentName")
  GraduationRequirement findByNameAndEntryYear(
      @Param("entryYear") EntryYear entryYear,
      @Param("departmentName") String departmentName,
      @Param("engLv") EnglishLevel englishLevel
  );
}
