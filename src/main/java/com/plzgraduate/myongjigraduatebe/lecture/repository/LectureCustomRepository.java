package com.plzgraduate.myongjigraduatebe.lecture.repository;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;

public interface LectureCustomRepository {

  List<SearchedLecture> searchByLectureCode(String lectureCode);

  List<SearchedLecture> searchByLectureName(String lectureName);
}
