package com.plzgraduate.myongjigraduatebe.lecture.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchLectureInfo;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.service.LectureService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/lectures")
public class LectureApiController {

  private final LectureService lectureService;

  public
  @GetMapping() List<SearchedLecture> searchLectures(
      @ModelAttribute SearchLectureInfo searchLectureInfo
  ) {
    return lectureService.searchLecture(
        searchLectureInfo.getKeyword(),
        searchLectureInfo.getQtype()
    );
  }

}
