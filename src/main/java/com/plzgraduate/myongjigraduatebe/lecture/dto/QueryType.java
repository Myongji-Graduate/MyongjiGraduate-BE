package com.plzgraduate.myongjigraduatebe.lecture.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueryType {
  LECTURE_NAME("과목명"),
  LECTURE_CODE("과목코드");

  private final String qType;
}
