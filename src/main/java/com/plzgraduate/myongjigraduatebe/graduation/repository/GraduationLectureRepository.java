package com.plzgraduate.myongjigraduatebe.graduation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;

public interface GraduationLectureRepository extends JpaRepository<GraduationLecture, Long> {

  @Query(value =
      "select l from GraduationLecture l join fetch l.lectureCategory join fetch l.lecture where l.graduationRequirement.id = (select r.id from GraduationRequirement r where r.department = :department and r.entryYear = :entryYear)")
  List<GraduationLecture> findAllByDepartmentAndEntryYearWithFetchJoin(
      @Param("department") Department department,
      @Param("entryYear") EntryYear entryYear
  );

}
