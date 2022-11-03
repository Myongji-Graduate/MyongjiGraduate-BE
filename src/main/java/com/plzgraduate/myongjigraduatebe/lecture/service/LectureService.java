package com.plzgraduate.myongjigraduatebe.lecture.service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;
import com.plzgraduate.myongjigraduatebe.lecture.dto.QueryType;

public interface LectureService {
  AllSearchedLecturesResponse searchLecture(
      String keyword,
      QueryType qType
  );
}
