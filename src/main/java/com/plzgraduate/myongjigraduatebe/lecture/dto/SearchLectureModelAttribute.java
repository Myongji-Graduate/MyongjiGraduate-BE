package com.plzgraduate.myongjigraduatebe.lecture.dto;

import java.util.Locale;

import lombok.Getter;

@Getter
public class SearchLectureModelAttribute {

  private final String keyword;
  private final QueryType qtype;

  public SearchLectureModelAttribute(
      String keyword,
      String qtype
  ) {
    this.keyword = keyword;
    this.qtype = QueryType.valueOf(qtype.toUpperCase(Locale.ROOT));
  }
}
