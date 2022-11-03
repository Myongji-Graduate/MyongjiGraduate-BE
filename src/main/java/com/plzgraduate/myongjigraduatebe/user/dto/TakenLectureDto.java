package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLectureDto {
  private final String code;
  private final String name;
  private final int credit;

  private TakenLectureDto(
      String code,
      String name,
      int credit
  ) {
    this.code = code;
    this.name = name;
    this.credit = credit;
  }

  public static TakenLectureDto from(Lecture lecture) {
    return new TakenLectureDto(lecture.getCode(), lecture.getName(), lecture.getCredit());
  }
}
