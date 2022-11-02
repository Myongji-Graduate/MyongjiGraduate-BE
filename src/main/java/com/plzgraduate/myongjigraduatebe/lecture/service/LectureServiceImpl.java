package com.plzgraduate.myongjigraduatebe.lecture.service;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureCustomRepository;
import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureCustomRepository lectureCustomRepository;

  private static final String QUERY_TYPE1 = "과목명";
  private static final String QUERY_TYPE2 = "과목코드";

  @Override
  public AllSearchedLecturesResponse searchLecture(
      String keyword,
      String qType
  ) {
    if (qType.equals(QUERY_TYPE1)) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureName(keyword));
    }
    if (qType.equals(QUERY_TYPE2)) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureCode(keyword));
    }
    return null;
  }
}
