package com.plzgraduate.myongjigraduatebe.lecture.repository;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;

public interface LectureCustomRepository {

  List<SearchedLecture> findByLectureCodeLike(String lectureCode);

  List<SearchedLecture> findByLectureNameBy(String lectureName);
}
