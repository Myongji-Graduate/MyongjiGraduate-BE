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
    this.entryYear = entryYear;
    this.department = department;
    this.file = file;
  }
}
