package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.ArrayList;
import java.util.List;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;

import lombok.Getter;

@Getter
public class ParsingTextDto {

  private final StudentNumber studentNumber;
  private final List<LectureCode> takenLectureCods;

  public ParsingTextDto(
      String parsingText
  ) {
    this.studentNumber = getStudentNumber(parsingText);
    this.takenLectureCods = getTakenLectureCodes(parsingText);
  }

  private StudentNumber getStudentNumber(String parsingText) {
    return StudentNumber.of(parsingText.split(", ")[1].substring(4, 12));
  }

  private List<LectureCode> getTakenLectureCodes(
      String parsingText
  ) {
    String[] splitText = parsingText.split(", ")[27].split(",");
    List<LectureCode> takenLectureCods = new ArrayList<>();
    for (int i = 9; i < splitText.length; i += 7) {
      if (Character.isDigit(splitText[i + 3].charAt(0))) {
        return takenLectureCods;
      }
      String code = splitText[i + 3];
      if (i + 7 < splitText.length && !splitText[i + 7].isBlank() && Character.isDigit(splitText[i + 7].charAt(0))) {
        i++;
      }
      takenLectureCods.add(LectureCode.of(code));
    }
    return takenLectureCods;
  }
}
