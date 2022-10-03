package com.plzgraduate.myongjigraduatebe.graduation.repository;

import java.util.List;
import java.util.Optional;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

public interface GraduationRepository {
  Optional<GraduationRequirement> findRequirementByDepartmentAndEntryYear(
      Department department,
      int entryYear
  );

  List<GraduationLecture> findAllLectureByRequirementAndLectureCategoryStartsWith(
      GraduationRequirement requirement,
      LectureCategory commonCulture
  );

  List<GraduationLecture> findAllGraduationLecture(GraduationRequirement requirement);

  List<GraduationLecture> findAllByGraduationRequirementAndLectureIsIn(
      GraduationRequirement requirement,
      List<Lecture> takenLectures
  );
}
