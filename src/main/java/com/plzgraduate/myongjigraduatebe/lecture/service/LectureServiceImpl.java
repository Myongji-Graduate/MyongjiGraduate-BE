package com.plzgraduate.myongjigraduatebe.lecture.service;

import static com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType.*;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureCustomRepository lectureCustomRepository;

  @Override
  public AllSearchedLecturesResponse searchLecture(
      String keyword,
      String qType
  ) {
    if (qType.equals(LECTURE_NAME.getQType())) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureName(keyword));
    }
    if (qType.equals(LECTURE_CODE.getQType())) {
      return AllSearchedLecturesResponse.from(lectureCustomRepository.searchByLectureCode(keyword));
    }
    return null;
  }
}
