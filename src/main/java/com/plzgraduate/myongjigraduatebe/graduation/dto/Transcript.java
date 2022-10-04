package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Transcript {

  private final String studentName;
  private final String studentNumber;
  private final String departmentName;
  private final EnglishLevel englishLevel;
  private final double takenCredit;
  private final double commonCultureCredit;
  private final int coreCultureCredit;
  private final int basicAcademicalCultureCredit;
  private final int normalCultureCredit;
  private final int majorCredit;
  private final int freeElectiveCredit;
  private final List<LectureCode> takenLectureCodes;

  @Builder
  private Transcript(
      String departmentName,
      String studentName,
      String studentNumber,
      EnglishLevel englishLevel,
      double commonCultureCredit,
      int coreCultureCredit,
      int basicAcademicalCultureCredit,
      int normalCultureCredit,
      int majorCredit,
      int freeElectiveCredit,
      double takenCredit,
      List<LectureCode> takenLectureCodes
  ) {
    this.departmentName = departmentName;
    this.studentName = studentName;
    this.studentNumber = studentNumber;
    this.englishLevel = englishLevel;
    this.commonCultureCredit = commonCultureCredit;
    this.coreCultureCredit = coreCultureCredit;
    this.basicAcademicalCultureCredit = basicAcademicalCultureCredit;
    this.normalCultureCredit = normalCultureCredit;
    this.majorCredit = majorCredit;
    this.freeElectiveCredit = freeElectiveCredit;
    this.takenCredit = takenCredit;
    this.takenLectureCodes = takenLectureCodes;
  }

  public int getEntryYear() {
    return Integer.parseInt(studentNumber.substring(2, 4));
  }
}
