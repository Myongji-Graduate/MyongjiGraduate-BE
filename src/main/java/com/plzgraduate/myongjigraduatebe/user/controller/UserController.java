package com.plzgraduate.myongjigraduatebe.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/user")
public class UserController {

  private final TakenLectureService takenLectureService;

  @PostMapping("/{id}/taken-lecture")
  @ResponseStatus(HttpStatus.OK)
  public String save(
      @PathVariable("id") Long userId,
      @RequestBody String parsingText
  ) {
    String[] userText = parsingText.split(", ");
    String[] lectureText = parsingText.split(", ")[27].split(",");
    takenLectureService.saveTakenLecture(userId, lectureText);
    return "redirect:/{id}/taken-lecture";
  }

}
