package com.plzgraduate.myongjigraduatebe.user.service;

import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;

public interface TakenLectureService {
  void saveTakenLecture(
      Long userId,
      String[] lectureText
  );

  TakenLectureResponse showTakenLecture(Long userId);

}
