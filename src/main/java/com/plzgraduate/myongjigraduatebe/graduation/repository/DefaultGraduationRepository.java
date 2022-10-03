package com.plzgraduate.myongjigraduatebe.graduation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class DefaultGraduationRepository implements GraduationRepository {

  private final GraduationLectureRepository lectureRepository;
  private final GraduationRequirementRepository requirementRepository;

  @Override
  public Optional<GraduationRequirement> findRequirementByDepartmentAndEntryYear(
      Department department,
      int entryYear
  ) {
    return requirementRepository.findByDepartmentAndEntryYear(department, entryYear);
  }

  @Override
  public List<GraduationLecture> findAllLectureByRequirementAndLectureCategoryStartsWith(
      GraduationRequirement requirement,
      LectureCategory category
  ) {
    return lectureRepository.findAllByGraduationRequirementAndLectureCategoryStartsWith(
        requirement,
        category
    );
  }

  @Override
  public List<GraduationLecture> findAllGraduationLecture(GraduationRequirement requirement) {
    return lectureRepository.findAllByGraduationRequirement(requirement);
  }

  public List<GraduationLecture> findAllByGraduationRequirementAndLectureIsIn(GraduationRequirement requirement, List<Lecture> takenLectures) {
    return lectureRepository.findAllByGraduationRequirementAndLectureIsIn(requirement, takenLectures);
  }
}
