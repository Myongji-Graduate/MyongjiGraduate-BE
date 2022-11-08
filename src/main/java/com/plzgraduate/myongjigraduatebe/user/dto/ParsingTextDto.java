package com.plzgraduate.myongjigraduatebe.user.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;

import lombok.Getter;

@Getter
public class ParsingTextDto {
  private final String[] splitText;

  public ParsingTextDto(
      String parsingText
  ) {
    splitText = parsingText.split("[|]");
  }

  public String getStudentName(){
    return splitText[2].split(", ")[1].split("[(]")[0];
  }

  public StudentNumber getStudentNumber() {

    return StudentNumber.valueOf(splitText[2].split(", ")[1].split("[(]")[1].substring(0,8));
  }

  public String getStudentDepartment(){
    String[] detailDepartment = splitText[2].split(", ")[0].split(" ");
    return detailDepartment[detailDepartment.length-1];
  }

  public List<LectureCode> getTakenLectureCodes() {
    List<LectureCode> takenLectureCodes = new ArrayList<>();
    for (int i = 16; i < splitText.length; i += 7) {
      if (i+3 < splitText.length && !Pattern.matches("^[A-Z]+$", splitText[i+3].substring(0, 1))) {
        return takenLectureCodes;
      }
      String code = splitText[i + 3];
      char grade = splitText[i+6].charAt(0);
      if(grade!='F' && grade!='N' && grade!='R'){
        takenLectureCodes.add(LectureCode.of(code));
      }
      if (i + 7 < splitText.length && Character.isDigit(splitText[i + 7].charAt(0))) {
        i++;
      }
    }
    return takenLectureCodes;
  }
}
