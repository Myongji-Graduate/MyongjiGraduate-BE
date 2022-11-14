package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.user.entity.Semester;
import com.plzgraduate.myongjigraduatebe.user.entity.Year;

import lombok.Getter;

@Getter
public class TakenLectureDto {
  private final Year year;

  private final Semester semester;

  private final LectureCode lectureCode;

  private TakenLectureDto(
      Year year,
      Semester semester,
      LectureCode lectureCode
  ) {
    this.year = year;
    this.semester = semester;
    this.lectureCode = lectureCode;
  }

  public static TakenLectureDto of(
      Year year,
      Semester semester,
      LectureCode lectureCode
  ) {
    return new TakenLectureDto(year, semester, lectureCode);
  }
}
