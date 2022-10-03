package com.plzgraduate.myongjigraduatebe.graduation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class TakenLecture {
  private final String category;
  private final String code;
  private final String name;
  private final int credit;
  private final String grade;
  private final boolean reduplication;

  @Builder
  public TakenLecture(
      String category,
      String code,
      String name,
      int credit,
      String grade,
      boolean reduplication
  ) {
    this.category = category;
    this.code = code;
    this.name = name;
    this.credit = credit;
    this.grade = grade;
    this.reduplication = reduplication;
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

    TakenLecture tl = (TakenLecture)o;
    return this
        .getCode()
        .equals(tl.getCode());
  }

  @Override
  public int hashCode() {
    int hash = 7;

    hash = 31 * hash + code.hashCode();

    return hash;
  }
}
