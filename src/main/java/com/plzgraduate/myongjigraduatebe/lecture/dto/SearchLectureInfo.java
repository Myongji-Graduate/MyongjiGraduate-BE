package com.plzgraduate.myongjigraduatebe.lecture.dto;


import lombok.Getter;

@Getter
public class SearchLectureInfo {

  private final String keyword;
  private final String qtype;

  public SearchLectureInfo(
      String keyword,
      String qtype
  ) {
    this.keyword = keyword;
    this.qtype = qtype;
  }
}
