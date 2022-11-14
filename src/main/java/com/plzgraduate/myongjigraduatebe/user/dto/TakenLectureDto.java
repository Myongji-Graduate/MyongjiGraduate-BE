package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.user.entity.Semester;
import com.plzgraduate.myongjigraduatebe.user.entity.Year;

import lombok.Getter;

@Getter
public class TakenLectureDto {
  private final String category;
  private final Year year;
  private final Semester semester;
  private final LectureCode lectureCode;
  private final String name;
  private final int credit;

  private TakenLectureDto(
      String category,
      Year year,
      Semester semester,
      LectureCode lectureCode,
      String name,
      int credit
  ) {
    this.category = category;
    this.year = year;
    this.semester = semester;
    this.lectureCode = lectureCode;
    this.name = name;
    this.credit = credit;
  }

  public static TakenLectureDto of(
      String category,
      Year year,
      Semester semester,
      LectureCode lectureCode,
      String name,
      int credit
  ) {
    return new TakenLectureDto(category, year, semester, lectureCode, name, credit);
  }
}
