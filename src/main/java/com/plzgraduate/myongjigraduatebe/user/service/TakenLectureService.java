package com.plzgraduate.myongjigraduatebe.user.service;

import java.util.List;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.user.dto.EditedTakenLecture;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;

public interface TakenLectureService {
  void saveTakenLecture(
      AuthenticatedUser user,
      ParsingTextDto parsingTextDto
  );

  TakenLectureResponse showTakenLecture(AuthenticatedUser user);

  void editTakenLecture(
      AuthenticatedUser user,
      EditedTakenLecture editedTakenLecture
  );

  List<TakenLecture> findAllByUserId(long id);
}
