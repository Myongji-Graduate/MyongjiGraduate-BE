package com.plzgraduate.myongjigraduatebe.graduation.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

public interface GraduationLectureRepository extends JpaRepository<GraduationLecture, Long> {

  List<GraduationLecture> findAllByGraduationRequirementAndLectureCategoryStartsWith(
      GraduationRequirement graduationRequirement,
      LectureCategory lectureCategory
  );

  List<GraduationLecture> findAllByGraduationRequirement(GraduationRequirement graduationRequirement);

  List<GraduationLecture> findAllByGraduationRequirementAndLectureIsIn(
      GraduationRequirement requirement,
      List<Lecture> takenLectures);
}
