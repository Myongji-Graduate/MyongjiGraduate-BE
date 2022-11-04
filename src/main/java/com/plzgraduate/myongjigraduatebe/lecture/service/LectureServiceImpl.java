package com.plzgraduate.myongjigraduatebe.lecture.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchLectureInfo;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureCustomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LectureServiceImpl implements LectureService {

  private final LectureCustomRepository lectureCustomRepository;

  @Override
  public List<SearchedLecture> searchLecture(SearchLectureInfo searchLectureInfo) {
    QueryType searchedKeyWord = QueryType.from(searchLectureInfo.getQtype());
    switch (searchedKeyWord) {
      case NAME:
        List<SearchedLecture> byLectureNameLike = lectureCustomRepository
            .findByLectureNameLike(searchLectureInfo
                                       .getKeyword()
                                       .replaceAll(" ", ""));
        byLectureNameLike.sort(Comparator.comparingInt(s -> s.getName().length()));
        return byLectureNameLike;
      case CODE:
        return lectureCustomRepository.findByLectureCodeLike(LectureCode.of(searchLectureInfo.getKeyword()));
      default:
        throw new IllegalStateException("검색어 타입이 잘못되었습니다.");
    }
  }
}
