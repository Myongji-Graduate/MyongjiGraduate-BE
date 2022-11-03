package com.plzgraduate.myongjigraduatebe.user.service;

import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;

public interface TakenLectureService {
  void saveTakenLecture(
      long userId,
      ParsingTextDto parsingTextDto
  );

  TakenLectureResponse showTakenLecture(long userId);

}
