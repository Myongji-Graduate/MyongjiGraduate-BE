package com.plzgraduate.myongjigraduatebe.user.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class StudentFindIdResponse {

  private final String userId;

  private final String studentNumber;

  public static StudentFindIdResponse of(
      String userId,
      String studentNumber
  ) {
    return StudentFindIdResponse.builder()
            .userId(userId)
            .studentNumber(studentNumber)
            .build();
  }
}
