package com.plzgraduate.myongjigraduatebe.lecture.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.lecture.dto.AllSearchedLecturesResponse;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchLectureModelAttribute;
import com.plzgraduate.myongjigraduatebe.lecture.service.LectureService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/lectures")
public class LectureApiController {

  private final LectureService lectureService;

  public
  @GetMapping() AllSearchedLecturesResponse searchLectures(
      @ModelAttribute SearchLectureModelAttribute searchLectureModelAttribute
  ) {
    return lectureService.searchLecture(
        searchLectureModelAttribute.getKeyword(),
        searchLectureModelAttribute.getQtype()
    );
  }

}
