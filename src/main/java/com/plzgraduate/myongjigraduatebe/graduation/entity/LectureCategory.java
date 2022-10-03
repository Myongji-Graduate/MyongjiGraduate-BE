package com.plzgraduate.myongjigraduatebe.graduation.entity;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LectureCategory {
  COMMON_CULTURE("공통교양", 17),
  COMMON_CULTURE_CHRISTIAN("공통교양(기독교)", 4),
  COMMON_CULTURE_CHAPEL("공통교양(채플)", 4),
  COMMON_CULTURE_EXPRESSION("공통교양(사고와 표현)", 3),
  COMMON_CULTURE_ENGLISH("공통교양(영어)", 6),
  COMMON_CULTURE_CAREER("공통교양(진로와 선택)", 2),
  CORE_CULTURE("핵심교양", 12),
  CORE_CULTURE_HISTORY_AND_PHILOSOPHY("핵심교양(역사와 철학)", 3),
  CORE_CULTURE_SOCIETY_AND_COMMUNITY("핵심교양(사회와 공동체)", 3),
  CORE_CULTURE_CULTURE_AND_ART("핵심교양(문화와 예술)", 3),
  CORE_CULTURE_SCIENCE_AND_TECHNOLOGY("핵심교양(과학과 기술)", 3),
  BASIC_ACADEMICAL_CULTURE("학문기초교양", 0),
  MAJOR("전공", 0);

  private final String koreanName;
  private final int totalCredit;

  public static List<LectureCategory> findAllCommonCategory() {
    return List.of(
        COMMON_CULTURE_CHRISTIAN,
        COMMON_CULTURE_CAREER,
        COMMON_CULTURE_EXPRESSION,
        COMMON_CULTURE_ENGLISH
    );
  }

  public static List<LectureCategory> findAllCoreCategory() {
    return List.of(
        CORE_CULTURE_CULTURE_AND_ART,
        CORE_CULTURE_HISTORY_AND_PHILOSOPHY,
        CORE_CULTURE_SCIENCE_AND_TECHNOLOGY,
        CORE_CULTURE_SOCIETY_AND_COMMUNITY
    );
  }
}
