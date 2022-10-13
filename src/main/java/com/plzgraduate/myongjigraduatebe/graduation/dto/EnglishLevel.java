package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

import lombok.Getter;

@Getter
public enum EnglishLevel {
  LOW(List.of(new LectureCode("KMA02106"), new LectureCode("KMA02107"), new LectureCode("KMA02108"), new LectureCode("KMA02109"))),
  MIDDLE(List.of(new LectureCode("KMA02123"), new LectureCode("KMA02124"), new LectureCode("KMA02125"), new LectureCode("KMA02126"))),
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
