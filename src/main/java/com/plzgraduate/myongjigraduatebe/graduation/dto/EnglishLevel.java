package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Getter;

@Getter
public enum EnglishLevel {
  LOW(List.of(LectureCode.of("KMA02106"), LectureCode.of("KMA02107"), LectureCode.of("KMA02108"), LectureCode.of("KMA02109"))),
  MIDDLE(List.of(LectureCode.of("KMA02123"), LectureCode.of("KMA02124"), LectureCode.of("KMA02125"), LectureCode.of("KMA02126"))),
  HIGH(List.of());

  public static final int STANDARD_SCORE = 600;
  private final List<LectureCode> lectureCodeList;

  EnglishLevel(List<LectureCode> lectureCodeList) {
    this.lectureCodeList = lectureCodeList;
  }

  public static EnglishLevel parse(String text) {
    String[] splitText = text.split(",");
    boolean exemption = !(splitText[1].split(" ")[3].equals("면제없음"));
    int score = Integer.parseInt(splitText[0].split(" ")[2]);

    if (exemption) {
      return HIGH;
    }

    return score <= STANDARD_SCORE ? LOW : MIDDLE;
  }

}
