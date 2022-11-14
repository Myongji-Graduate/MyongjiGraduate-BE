package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.Comparator;
import java.util.List;

import com.plzgraduate.myongjigraduatebe.user.entity.Semester;

import lombok.Getter;

@Getter
public class TakenLectureResponse {

  private final int totalCredit;
  private final List<ShownTakenLectureDto> takenLectures;

  private TakenLectureResponse(
      int totalCredit,
      List<ShownTakenLectureDto> shownTakenLectureDtoList
  ) {
    this.totalCredit = totalCredit;
    this.takenLectures = shownTakenLectureDtoList;
  }

  public static TakenLectureResponse of(List<ShownTakenLectureDto> takenLectureList) {
    sortTakenLecture(takenLectureList);
    int totalCredit = takenLectureList
        .stream()
        .mapToInt(takenLecture -> takenLecture.getCredit())
        .sum();
    return new TakenLectureResponse(totalCredit, takenLectureList);
  }

  private static void sortTakenLecture(List<ShownTakenLectureDto> takenLectureList) {
    takenLectureList
        .sort(Comparator.comparing(ShownTakenLectureDto::getYear, Comparator.nullsFirst(Comparator.reverseOrder()))
                          .thenComparing(
            ShownTakenLectureDto::getSemester,
            Comparator.nullsFirst(Comparator.reverseOrder())
        ));
  }
}
