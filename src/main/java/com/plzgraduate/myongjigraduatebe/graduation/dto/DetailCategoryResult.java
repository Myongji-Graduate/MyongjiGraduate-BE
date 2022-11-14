package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.dto.LectureResponse;

import lombok.Getter;

@Getter
public class DetailCategoryResult {

  private final String detailCategoryName;
  private final int totalCredits;
  private int takenCredits = 0;
  private boolean isCompleted = false;
  private final List<LectureResponse> takenMandatoryLectures = new ArrayList<>();
  private final List<LectureResponse> haveToMandatoryLectures = new LinkedList<>();
  private final List<LectureResponse> takenElectiveLectures = new ArrayList<>();
  private final List<LectureResponse> haveToElectiveLectures = new LinkedList<>();

  public DetailCategoryResult(
      LectureCategory lectureCategory
  ) {
    this.detailCategoryName = lectureCategory.getDetailCategory();
    this.totalCredits = lectureCategory.getTotalCredit();
  }

  public void add(
      GraduationLecture graduationLecture,
      boolean taken
  ) {
    LectureResponse lectureResponse = LectureResponse.from(graduationLecture.getLecture());

    if (taken) {
      takenCredits += lectureResponse.getCredit();
    }

    if (taken && graduationLecture.isMandatory()) {
      takenMandatoryLectures.add(lectureResponse);
      return;
    }

    if (taken && !graduationLecture.isMandatory()) {
      takenElectiveLectures.add(lectureResponse);
      return;
    }

    if (!taken && graduationLecture.isMandatory()) {
      haveToMandatoryLectures.add(lectureResponse);
      return;
    }

    haveToElectiveLectures.add(lectureResponse);
  }

  public void checkCompleted() {
    isCompleted = haveToMandatoryLectures.isEmpty() && totalCredits <= takenCredits;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DetailCategoryResult that = (DetailCategoryResult)o;
    return Objects.equals(detailCategoryName, that.detailCategoryName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(detailCategoryName);
  }

  public int getLeftCredit() {
    return Math.max(0, takenCredits - totalCredits);
  }
}
