package com.plzgraduate.myongjigraduatebe.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

  private final TakenLectureService takenLectureService;

  @GetMapping("/{id}/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public TakenLectureResponse show(@PathVariable("id") Long id){
    return takenLectureService.showTakenLecture(id);
  }

  @PostMapping("/{id}/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public String save(
      @PathVariable("id") Long id,
      @RequestBody String parsingText
  ) {
    String[] userText = parsingText.split(", ");
    String[] lectureText = parsingText.split(", ")[27].split(",");
    takenLectureService.saveTakenLecture(id, lectureText);
    return "redirect:/{id}/taken-lecture";
  }

}
