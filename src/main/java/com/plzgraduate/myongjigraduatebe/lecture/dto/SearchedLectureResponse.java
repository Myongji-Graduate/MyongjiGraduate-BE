package com.plzgraduate.myongjigraduatebe.lecture.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchedLectureResponse {

  private final LectureCode lectureCode;

  private final String name;

  private final int credit;

  public static SearchedLectureResponse from(SearchedLecture entity) {
    return new SearchedLectureResponse(entity.getLectureCode(), entity.getName(), entity.getCredit());
  }
}
