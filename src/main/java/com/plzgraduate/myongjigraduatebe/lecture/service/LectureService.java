package com.plzgraduate.myongjigraduatebe.lecture.service;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;

public interface LectureService {
  List<SearchedLecture> searchLecture(
      String keyword,
      QueryType qType
  );
}
