package com.plzgraduate.myongjigraduatebe.lecture.dto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AllSearchedLecturesResponse {

  private final List<SearchedLectureResponse> searchedLectures;

  public static AllSearchedLecturesResponse from(List<SearchedLecture> searchedLectures) {
    return new AllSearchedLecturesResponse(searchedLectures
                                               .stream()
                                               .map(SearchedLectureResponse::from)
                                               .collect(Collectors.toList()));
  }

}
