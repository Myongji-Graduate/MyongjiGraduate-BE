package com.plzgraduate.myongjigraduatebe.graduation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.plzgraduate.myongjigraduatebe.common.utils.PdfParser;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.service.GraduationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/graduation")
public class GraduationApiController {

  private final GraduationService graduationService;

  @PostMapping("result")
  @ResponseStatus(HttpStatus.OK)
  public String access(
      @RequestParam String entryYear,
      @RequestParam String department,
      @RequestParam MultipartFile file
  ) {
    GraduationRequest request = new GraduationRequest(entryYear, department, file);
    return PdfParser.parseString(request);
  }
}
