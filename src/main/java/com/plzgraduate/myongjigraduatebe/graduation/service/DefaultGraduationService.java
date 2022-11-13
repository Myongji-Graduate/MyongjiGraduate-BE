package com.plzgraduate.myongjigraduatebe.graduation.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BasicInfo;
import com.plzgraduate.myongjigraduatebe.graduation.dto.ChapelResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.DetailCategoryResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.DetailGraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DefaultGraduationService implements GraduationService {

  private final GraduationLectureRepository graduationLectureRepository;
  private final LectureRepository lectureRepository;
  private final TakenLectureService takenLectureService;

  @Override
  @Transactional(readOnly = true)
  public GraduationResult getResult(AuthenticatedUser user) {
    List<TakenLecture> takenLectures = takenLectureService.findAllByUserId(user.getId());
    List<GraduationLecture> graduationLectures = graduationLectureRepository.findAllByDepartmentAndEntryYearWithFetchJoin(
        user.getDepartment(),
        user.getEntryYear(),
        user.getEngLv()
    );

    return createResult(user, takenLectures, graduationLectures);
  }

  private GraduationResult createResult(
      AuthenticatedUser user,
      List<TakenLecture> takenLectures,
      List<GraduationLecture> graduationLectures
  ) {
    GraduationRequirement requirement = graduationLectures
        .get(0)
        .getGraduationRequirement();

    int takenCredit = 0;
    int takenChapelCount = 0;

    for (TakenLecture tl : takenLectures) {
      Lecture lecture = tl.getLecture();
      takenCredit += lecture.getCredit();

      if (lecture
          .getName()
          .equals("채플")) {
        takenChapelCount++;
      }
    }

    ChapelResult chapelResult = new ChapelResult(takenChapelCount);

    BasicInfo basicInfo = getBasicInfo(user, requirement.getTotalCredit(), takenCredit);

    GraduationResult graduationResult = new GraduationResult(basicInfo, chapelResult);

    Set<Lecture> takenLectureSet = takenLectures
        .stream()
        .map(TakenLecture::getLecture)
        .collect(Collectors.toSet());

    updateGraduationCategoricalLectures(requirement, graduationLectures, graduationResult, takenLectureSet);

    DetailGraduationResult normalGraduationResult = DetailGraduationResult.createNormalCulture(requirement);
    DetailGraduationResult freeGraduationResult = DetailGraduationResult.createFreeElective(requirement);
    updateLeftLectures(takenLectureSet, normalGraduationResult, freeGraduationResult);

    graduationResult.setNormalResult(normalGraduationResult);
    graduationResult.setFreeElectiveResult(freeGraduationResult);

    graduationResult.checkGraduation();

    return graduationResult;
  }

  private void updateGraduationCategoricalLectures(
      GraduationRequirement requirement,
      List<GraduationLecture> graduationLectures,
      GraduationResult graduationResult,
      Set<Lecture> takenLectureSet
  ) {
    Map<GraduationCategory, List<GraduationLecture>> categoryToGraduationLecture = makeCategoryMap(graduationLectures);

    List<Lecture> duplicatedLectures = lectureRepository.findAllByDuplicatedLectureCodeNotNull();
    Map<LectureCode, Lecture> duplicatedLectureCodeToLecture = new HashMap<>(duplicatedLectures.size());
    duplicatedLectures.forEach(lecture -> duplicatedLectureCodeToLecture.put(lecture.getLectureCode(), lecture));

    for (Map.Entry<GraduationCategory, List<GraduationLecture>> entry : categoryToGraduationLecture.entrySet()) {

      DetailGraduationResult detailGraduationResult = getGraduationResult(
          requirement,
          duplicatedLectureCodeToLecture,
          takenLectureSet,
          entry
      );

      setGraduationResult(graduationResult, entry, detailGraduationResult);
    }
  }

  private void updateLeftLectures(
      Set<Lecture> takenLectureSet,
      DetailGraduationResult normalGraduationResult,
      DetailGraduationResult freeGraduationResult
  ) {
    for (Lecture leftLecture : takenLectureSet) {
      if (leftLecture.isMajor()) {
        freeGraduationResult.addTakenCredit(leftLecture.getCredit());
      } else {
        normalGraduationResult.addTakenCredit(leftLecture.getCredit());
      }
    }
  }

  private void setGraduationResult(
      GraduationResult graduationResult,
      Map.Entry<GraduationCategory, List<GraduationLecture>> entry,
      DetailGraduationResult detailGraduationResult
  ) {

    switch (entry.getKey()) {
      case COMMON_CULTURE:
        graduationResult.setCommonCultureResult(detailGraduationResult);
        return;
      case CORE_CULTURE:
        graduationResult.setCoreCultureResult(detailGraduationResult);
        return;
      case BASIC_ACADEMICAL_CULTURE:
        graduationResult.setBasicAcademicalCultureResult(detailGraduationResult);
        return;
      case MAJOR:
        graduationResult.setMajorResult(detailGraduationResult);
    }
  }

  private BasicInfo getBasicInfo(
      AuthenticatedUser user,
      int totalCredit,
      int takenCredit
  ) {
    return BasicInfo
        .builder()
        .name(
            user.getName())
        .studentNumber(user
                           .getStudentNumber()
                           .getValue())
        .department(user.getDepartment())
        .totalCredit(totalCredit)
        .takenCredit(takenCredit)
        .build();
  }

  private DetailGraduationResult getGraduationResult(
      GraduationRequirement requirement,
      Map<LectureCode, Lecture> duplicatedLectures,
      Set<Lecture> takenLectures,
      Map.Entry<GraduationCategory, List<GraduationLecture>> entry
  ) {
    GraduationCategory category = entry.getKey();
    List<GraduationLecture> categoryGraduationLectures = entry.getValue();

    int takenCredit = 0;
    Map<LectureCategory, DetailCategoryResult> categoryToResult = new HashMap<>(categoryGraduationLectures.size());

    for (GraduationLecture gl : categoryGraduationLectures) {
      Lecture lecture = gl.getLecture();
      LectureCategory lectureCategory = gl.getLectureCategory();
      DetailCategoryResult detailCategoryResult = categoryToResult.getOrDefault(
          lectureCategory,
          new DetailCategoryResult(lectureCategory)
      );

      boolean taken = isTaken(duplicatedLectures, takenLectures, lecture);

      if (taken) {
        takenLectures.remove(lecture);
        takenCredit += lecture.getCredit();
      }

      detailCategoryResult.add(gl, taken);
      detailCategoryResult.checkCompleted();
      categoryToResult.put(lectureCategory, detailCategoryResult);
    }

    return DetailGraduationResult
        .builder()
        .categoryName(category.name())
        .totalCredit(requirement.getCategoricalTotalCredit(category))
        .takenCredit(takenCredit)
        .detailCategory(categoryToResult.values())
        .build();
  }

  private boolean isTaken(
      Map<LectureCode, Lecture> lectureCodeToLecture,
      Set<Lecture> takenLectures,
      Lecture lecture
  ) {
    boolean isTaken = takenLectures.contains(lecture);

    if (isTaken) {
      return true;
    }

    LectureCode duplicatedLectureCode = lecture.getDuplicatedLectureCode();

    while (!isTaken && duplicatedLectureCode != null) {
      Lecture duplicatedLecture = lectureCodeToLecture.get(duplicatedLectureCode);

      if (duplicatedLecture != null) {
        isTaken = duplicatedLecture.equals(lecture);
        continue;
      }

      Lecture lastDuplicatedLecture = lectureRepository
          .findByLectureCode(duplicatedLectureCode)
          .orElse(null);

      if (lastDuplicatedLecture != null) {
        isTaken = lastDuplicatedLecture.equals(lecture);
      }
      break;
    }

    return isTaken;
  }

  private Map<GraduationCategory, List<GraduationLecture>> makeCategoryMap(List<GraduationLecture> graduationLectures) {
    Map<GraduationCategory, List<GraduationLecture>> map = new HashMap<>(graduationLectures.size());

    graduationLectures.forEach(gl -> {
      GraduationCategory category = gl
          .getLectureCategory()
          .getCategory();

      if (!map.containsKey(category)) {
        map.put(category, new ArrayList<>());
      }

      map
          .get(category)
          .add(gl);
    });

    return map;
  }
}
