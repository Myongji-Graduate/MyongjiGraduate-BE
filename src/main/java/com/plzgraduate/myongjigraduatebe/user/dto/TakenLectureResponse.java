package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class TakenLectureResponse {
  private final List<TakenLectureDto> takenLectureDtoList;

  public TakenLectureResponse(List<TakenLectureDto> takenLectureDtoList) {
    this.takenLectureDtoList = takenLectureDtoList;
  }
}
