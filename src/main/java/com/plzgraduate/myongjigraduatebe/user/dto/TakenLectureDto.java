package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Getter;

@Getter
public class TakenLectureDto {
  private final String year;

  private final String semester;

  private final LectureCode lectureCode;

  private TakenLectureDto(
      String year,
      String semester,
      LectureCode lectureCode
  ) {
    this.year = year;
    this.semester = semester;
    this.lectureCode = lectureCode;
  }

  public static TakenLectureDto from(String year, String semester, LectureCode lectureCode){
    return new TakenLectureDto(year, semester, lectureCode);
  }
}
