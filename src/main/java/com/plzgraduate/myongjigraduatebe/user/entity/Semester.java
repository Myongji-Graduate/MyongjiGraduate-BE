package com.plzgraduate.myongjigraduatebe.user.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum Semester {
  FIRST("1학기", 1),
  SUMMER("하계계절",2),
  SECOND("2학기",3),
  WINTER("동계계절", 4);

  private final String semesterName;
  private final int value;
  Semester(String semesterName, int value){
    this.semesterName = semesterName;
    this.value = value;
  }

  @JsonCreator
  public static Semester of(String name){
    for(Semester semester : values()){
      if(semester.semesterName.equals(name)){
        return semester;
      }
    }
    throw new IllegalArgumentException("해당 학기가 존재하지 않습니다");
  }
}
