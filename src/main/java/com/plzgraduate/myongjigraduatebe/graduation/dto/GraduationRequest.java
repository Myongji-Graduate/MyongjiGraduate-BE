package com.plzgraduate.myongjigraduatebe.graduation.dto;

import java.util.regex.Pattern;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;

@Getter
public class GraduationRequest {

  private static final int startEntryYear = 16;
  private static final int endEntryYear = 22;
  private final String entryYear;
  private final String department;
  private final MultipartFile file;

  public GraduationRequest(
      String entryYear,
      String department,
      MultipartFile file
  ) {
    validate(entryYear, department, file);
    this.entryYear = entryYear;
    this.department = department;
    this.file = file;
  }

  private void validate(
      String entryYear,
      String department,
      MultipartFile file
  ) {
    if (entryYear.isEmpty()) {
      throw new IllegalArgumentException("입학년도를 입력해주세요");
    }
    validateEntryYear(entryYear);
    if (department.isEmpty()) {
      throw new IllegalArgumentException("학과을 선택해주세요");
    }
    if (file.isEmpty()) {
      throw new IllegalArgumentException("PDF를 찾을 수 없습니다");
    }
  }

  private void validateEntryYear(String entryYear) {
    String pattern = "^\\d*$";
    if (!Pattern.matches(pattern, entryYear)) {
      throw new IllegalArgumentException("문자가 포함되어 있습니다");
    }
    if (entryYear.length() != 2) {
      throw new IllegalArgumentException("16~22 사이를 입력해주세요");
    }
    if (Integer.parseInt(entryYear) < startEntryYear || Integer.parseInt(entryYear) > endEntryYear) {
      throw new IllegalArgumentException("16~22 사이를 입력해주세요");
    }
  }
}
