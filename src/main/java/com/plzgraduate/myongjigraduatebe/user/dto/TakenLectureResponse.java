package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.List;

import lombok.Getter;

@Getter
public class TakenLectureResponse {

  private final int totalCredit;
  private final List<ShownTakenLectureDto> takenLectures;

  private TakenLectureResponse(int totalCredit, List<ShownTakenLectureDto> shownTakenLectureDtoList) {
    this.totalCredit = totalCredit;
    this.takenLectures = shownTakenLectureDtoList;
  }

  public static TakenLectureResponse of(List<ShownTakenLectureDto> takenLectureList){
    int totalCredit = takenLectureList.stream().mapToInt(takenLecture -> takenLecture.getCredit()).sum();
    return new TakenLectureResponse(totalCredit, takenLectureList);
  }
}
