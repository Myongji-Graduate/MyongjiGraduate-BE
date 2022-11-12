package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lombok.Getter;

@Getter
public class TakenLectureResponse {

  private final int totalCredit;
  private final List<ShownTakenLectureDto> takenLectures;

  private static final HashMap<String, Integer> semesterSortStandard = new HashMap<>();

  private TakenLectureResponse(int totalCredit, List<ShownTakenLectureDto> shownTakenLectureDtoList) {
    this.totalCredit = totalCredit;
    this.takenLectures = shownTakenLectureDtoList;
  }


  public static TakenLectureResponse of(List<ShownTakenLectureDto> takenLectureList){
    initSemesterSortStandard();
    sortTakenLecture(takenLectureList);
    int totalCredit = takenLectureList.stream().mapToInt(takenLecture -> takenLecture.getCredit()).sum();
    return new TakenLectureResponse(totalCredit, takenLectureList);
  }

  private static void sortTakenLecture(List<ShownTakenLectureDto> takenLectureList){
    takenLectureList.sort((takenLecture1, takenLecture2) -> {
      if(takenLecture1.getYear().equals(takenLecture2.getYear())){
        return semesterSortStandard.getOrDefault(takenLecture2.getSemester(),0) - semesterSortStandard.getOrDefault(takenLecture1.getSemester(),0);
      }
      return -takenLecture1.getYear().compareTo(takenLecture2.getYear());
    } );
  }

  private static void initSemesterSortStandard(){
    semesterSortStandard.put("1학기", 1);
    semesterSortStandard.put("하계계절",2);
    semesterSortStandard.put("2학기",3);
    semesterSortStandard.put("동계계절",4);
    semesterSortStandard.put("커스텀", 5);
  }
}
