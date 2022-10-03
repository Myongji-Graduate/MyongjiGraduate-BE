package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationLecture;
import com.plzgraduate.myongjigraduatebe.graduation.entity.LectureCategory;
import com.plzgraduate.myongjigraduatebe.lecture.dto.LectureResponse;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

import lombok.Getter;

@Getter
public class DetailCategoryResult {

  private final String categoryName;
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
    this(lectureCategory, lectureCategory.getTotalCredit());
  }

  public DetailCategoryResult(
      LectureCategory lectureCategory,
      int totalCredits
  ) {
    this.categoryName = lectureCategory.getKoreanName();
    this.totalCredits = totalCredits;
  }

  public void addAllRequiredLectures(List<GraduationLecture> graduationLectures) {
    graduationLectures.forEach(this::addRequiredLecture);
  }

  private void addRequiredLecture(GraduationLecture graduationLecture) {
    Lecture lecture = graduationLecture.getLecture();
    LectureResponse lectureResponse = LectureResponse.from(lecture);

    if (graduationLecture.isMandatory()) {
      haveToMandatoryLectures.add(lectureResponse);
      return;
    }

    haveToElectiveLectures.add(lectureResponse);
  }

  public void addTakenLecture(GraduationLecture graduationLecture) {
    Lecture lecture = graduationLecture.getLecture();
    takenCredits += lecture.getCredit();
    LectureResponse lectureResponse = LectureResponse.from(lecture);

    if (graduationLecture.isMandatory()) {
      takenMandatoryLectures.add(lectureResponse);
      haveToMandatoryLectures.remove(lectureResponse);
      return;
    }

    takenElectiveLectures.add(lectureResponse);
    haveToElectiveLectures.remove(lectureResponse);
  }

  public void checkCompleted() {
    isCompleted = haveToMandatoryLectures.isEmpty() && totalCredits == takenCredits;
  }

}
