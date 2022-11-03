package com.plzgraduate.myongjigraduatebe.lecture.service;

import static com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType.*;

import java.util.List;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureCustomRepository lectureCustomRepository;

  @Override
  public List<SearchedLecture> searchLecture(
      String keyword,
      QueryType qType
  ) {
    if (qType.equals(NAME)) {
      return lectureCustomRepository.findByLectureNameBy(keyword);
    }
    if (qType.equals(CODE)) {
      return lectureCustomRepository.findByLectureCodeLike(LectureCode.of(keyword));
    }
    throw new IllegalStateException("검색어 타입이 잘못되었습니다.");
  }
}
