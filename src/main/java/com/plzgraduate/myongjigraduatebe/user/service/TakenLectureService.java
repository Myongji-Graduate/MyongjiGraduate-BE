package com.plzgraduate.myongjigraduatebe.user.service;

public interface TakenLectureService {
  void saveTakenLecture(
      Long userId,
      String[] lectureText
  );
}
