package com.plzgraduate.myongjigraduatebe.lecture.service;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchLectureInfo;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;

public interface LectureService {
  List<SearchedLecture> searchLecture(SearchLectureInfo searchLectureInfo);
}
