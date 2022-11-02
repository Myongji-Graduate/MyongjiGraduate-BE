package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Getter;

@Getter
public enum EnglishLevel {

  LOW(List.of(LectureCode.of("KMA02106"), LectureCode.of("KMA02107"), LectureCode.of("KMA02108"), LectureCode.of("KMA02109"))),
  MIDDLE(List.of(LectureCode.of("KMA02123"), LectureCode.of("KMA02124"), LectureCode.of("KMA02125"), LectureCode.of("KMA02126"))),
  HIGH(List.of());

  private final List<LectureCode> lectureCodeList;

  EnglishLevel(List<LectureCode> lectureCodeList) {
    this.lectureCodeList = lectureCodeList;
  }

  public static EnglishLevel of(int ordinal) {
    for (EnglishLevel engLv : values()) {
      if (engLv.ordinal() == ordinal) {
        return engLv;
      }
    }

    throw new IllegalArgumentException("영어 레벨을 확인해주세요");
  }
}
