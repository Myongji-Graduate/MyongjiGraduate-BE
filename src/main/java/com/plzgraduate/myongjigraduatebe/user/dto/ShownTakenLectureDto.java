package com.plzgraduate.myongjigraduatebe.user.dto;

import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;
import com.plzgraduate.myongjigraduatebe.user.entity.TakenLecture;

import lombok.Getter;

@Getter
public class ShownTakenLectureDto {

  private final String year;
  private final String semester;
  private final String code;
  private final String name;
  private final int credit;

  private ShownTakenLectureDto(
      String year,
      String semester,
      String code,
      String name,
      int credit
  ) {
    this.year = year;
    this.semester = semester;
    this.code = code;
    this.name = name;
    this.credit = credit;
  }

  public static ShownTakenLectureDto from(TakenLecture takenLecture) {
    Lecture lecture = takenLecture.getLecture();
    return new ShownTakenLectureDto(takenLecture.getTakenYear(),
                                    takenLecture.getTakenSemester(),
                                    lecture.getCode(),
                                    lecture.getName(),
                                    lecture.getCredit()
    );
  }
}
