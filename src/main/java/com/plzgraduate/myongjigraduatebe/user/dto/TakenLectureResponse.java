package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class TakenLectureResponse {
  private final List<TakenLectureDto> takenLectureDtoList;

  private TakenLectureResponse(List<TakenLectureDto> takenLectureDtoList) {
    this.takenLectureDtoList = takenLectureDtoList;
  }

  public static TakenLectureResponse of(List<TakenLectureDto> takenLectureDtoList){
    return new TakenLectureResponse(takenLectureDtoList);
  }

  public void addTakenLectureDto(TakenLectureDto takenLectureDto){
    takenLectureDtoList.add(takenLectureDto);
  }


}
