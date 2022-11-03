package com.plzgraduate.myongjigraduatebe.lecture.repository;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;

public interface LectureCustomRepository {

  List<SearchedLecture> findByLectureCodeLike(LectureCode lectureCode);

  List<SearchedLecture> findByLectureNameBy(String lectureName);
}
