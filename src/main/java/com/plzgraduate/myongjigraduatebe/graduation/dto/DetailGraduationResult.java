package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationCategory;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.lecture.dto.LectureResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DetailGraduationResult {

  private static final String MAJOR_CATEGORY_NAME = GraduationCategory.MAJOR.name();
  private static final Set<String> MAJOR_DETAIL_CATEGORY_NAME = new HashSet<>() {{
    add("BUSINESS");
    add("MANAGEMENT_INFORMATION");
    add("INTERNATIONAL_TRADE");
    add("ADMINISTRATION");
  }};

  private final String categoryName;
  private boolean isCompleted;
  private final int totalCredit;
  private int takenCredit;
  private final Collection<DetailCategoryResult> detailCategory;

  @Builder
  private DetailGraduationResult(
      String categoryName,
      int totalCredit,
      int takenCredit,
      Collection<DetailCategoryResult> detailCategory
  ) {
    this.categoryName = categoryName;
    this.totalCredit = totalCredit;
    this.takenCredit = takenCredit;
    this.detailCategory = detailCategory;
  }

  public static DetailGraduationResult createFreeElective(GraduationRequirement requirement) {
    return new DetailGraduationResult(
        GraduationCategory.FREE_ELECTIVE.name(),
        requirement.getFreeElectiveCredit(),
        0,
        null
    );
  }

  public static DetailGraduationResult createNormalCulture(GraduationRequirement requirement) {
    return new DetailGraduationResult(
        GraduationCategory.NORMAL_CULTURE.name(),
        requirement.getNormalCultureCredit(),
        0,
        null
    );
  }

  public void checkComplete() {
    boolean isCompleted = totalCredit <= takenCredit;

    if (detailCategory == null) {
      this.isCompleted = isCompleted;
      return;
    }

    this.isCompleted = isCompleted && checkDetailCategoryComplete();
  }

  private boolean checkDetailCategoryComplete() {
    boolean isCompleted = true;
    for (DetailCategoryResult categoryResult : detailCategory) {
      isCompleted = isCompleted && categoryResult.isCompleted();
    }
    return isCompleted;
  }

  public int getLeftCredit() {
    int leftCredit = 0;

    shiftLecture();

    if (detailCategory == null) {
      leftCredit = Math.max(0, takenCredit - totalCredit);
    } else {
      for (DetailCategoryResult result : detailCategory) {
        leftCredit += result.getLeftCredit();
      }
    }

    takenCredit -= leftCredit;
    return leftCredit;
  }

  public void addTakenCredit(int takenCredit) {
    this.takenCredit += takenCredit;
  }

  private void shiftLecture() {
    if (categoryName.equals(MAJOR_CATEGORY_NAME)) {
      List<LectureResponse> shiftLectures = new ArrayList<>();
      DetailCategoryResult mainResult = null;

      for (DetailCategoryResult result : detailCategory) {
        if (MAJOR_DETAIL_CATEGORY_NAME.contains(result.getDetailCategoryName())) {
          mainResult = result;
        }

        while (result.getLeftCredit() > 0) {
          LectureResponse shiftLecture = result.shiftLecture(result.getLeftCredit());
          shiftLectures.add(shiftLecture);
        }
      }

      if (mainResult != null) {
        mainResult.shift(shiftLectures);
      }
    }
  }
}
