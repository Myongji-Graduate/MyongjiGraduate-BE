package com.plzgraduate.myongjigraduatebe.lecture.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;

@Getter
public class SearchedLecture {

  private final long id;
  private final LectureCode lectureCode;
  private final String name;
  private final int credit;

  @QueryProjection
  public SearchedLecture(
      long id,
      LectureCode lectureCode,
      String name,
      int credit
  ) {
    this.id = id;
    this.lectureCode = lectureCode;
    this.name = name;
    this.credit = credit;
  }
}
