package com.plzgraduate.myongjigraduatebe.lecture.service;

import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;

public interface LectureService {
  AllSearchedLecturesResponse searchLecture(
      String keyword,
      String qType
  );
}
