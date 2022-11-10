package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Getter;

@Getter
public class SavedTakenLectureDto {
  private final String year;

  private final String semester;

  private final LectureCode lectureCode;

  private SavedTakenLectureDto(
      String year,
      String semester,
      LectureCode lectureCode
  ) {
    this.year = year;
    this.semester = semester;
    this.lectureCode = lectureCode;
  }

  public static SavedTakenLectureDto from(String year, String semester, LectureCode lectureCode){
    return new SavedTakenLectureDto(year, semester, lectureCode);
  }
}
