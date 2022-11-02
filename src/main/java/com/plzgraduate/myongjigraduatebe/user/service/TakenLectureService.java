package com.plzgraduate.myongjigraduatebe.user.service;

import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.User;

public interface TakenLectureService {
  void saveTakenLecture(
      Long userId,
      String[] lectureText
  );
}
