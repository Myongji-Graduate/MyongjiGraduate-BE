package com.plzgraduate.myongjigraduatebe.lecture.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

import lombok.Getter;

@Getter
public class LectureResponse {
  private final long id;
  private final String code;
  private final String name;
  private final int credit;

  private LectureResponse(
      long id,
      String code,
      String name,
      int credit
  ) {
    this.id = id;
    this.code = code;
    this.name = name;
    this.credit = credit;
  }

  public static LectureResponse from(Lecture lecture) {
    return new LectureResponse(
        lecture.getId(),
        lecture.getCode(),
        lecture.getName(),
        lecture.getCredit()
    );
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (o == this) {
      return true;
    }

    if (o.getClass() != getClass()) {
      return false;
    }

    LectureResponse lectureResponse = (LectureResponse)o;
    return this.getId() == lectureResponse.getId();
  }

  @Override
  public int hashCode() {
    int hash = 7;

    hash = (int)(31 * hash + getId());

    return hash;
  }
}
