package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class TakenLectureResponse {
  private final List<TakenLectureDto> takenLectures;

  private TakenLectureResponse(List<TakenLectureDto> takenLectureDtoList) {
    this.takenLectures = takenLectureDtoList;
  }

  public static TakenLectureResponse of(List<TakenLectureDto> takenLectureList){
    return new TakenLectureResponse(takenLectureList);
  }
}
