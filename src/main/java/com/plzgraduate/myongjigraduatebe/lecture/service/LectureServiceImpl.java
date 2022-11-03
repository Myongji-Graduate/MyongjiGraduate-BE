package com.plzgraduate.myongjigraduatebe.lecture.service;

import static com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType.*;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;
import com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureCustomRepository lectureCustomRepository;

  @Override
  public AllSearchedLecturesResponse searchLecture(
      String keyword,
      QueryType qType
  ) {
    if (qType.equals(NAME)) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureName(keyword));
    }
    if (qType.equals(CODE)) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureCode(keyword));
    }
    throw new IllegalStateException("검색어 타입이 잘못되었습니다.");
  }
}
