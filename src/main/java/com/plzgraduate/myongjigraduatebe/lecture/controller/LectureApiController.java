package com.plzgraduate.myongjigraduatebe.lecture.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.lecture.service.LectureService;
import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/lectures")
public class LectureApiController {

  private final LectureService lectureService;

  @GetMapping()
  public AllSearchedLecturesResponse searchedLectures(
      @RequestParam(name = "qtype") String qType,
      @RequestParam(name = "keyword") String keyword
  ) {
    return lectureService.searchLecture(keyword, qType);
  }

}
