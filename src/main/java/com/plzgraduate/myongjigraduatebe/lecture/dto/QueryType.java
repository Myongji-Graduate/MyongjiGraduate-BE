package com.plzgraduate.myongjigraduatebe.lecture.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum QueryType {
  NAME("name"),
  CODE("code");

  private final String keyWordType;

  public static QueryType from(String keyWordType) {
    for (QueryType queryType : values()) {
      if (queryType
          .getKeyWordType()
          .contains(keyWordType)) {
        return queryType;
      }
    }
    throw new IllegalArgumentException("쿼리타입을 생성할 수 없습니다.");
  }

}
