package com.plzgraduate.myongjigraduatebe.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
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
  public TakenLectureResponse show(@PathVariable("id") long id){
    return takenLectureService.showTakenLecture(id);
  }

  @PostMapping("/{id}/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public void save(
      @PathVariable("id") long id,
      @RequestBody String parsingText
  ) {
    takenLectureService.saveTakenLecture(id, new ParsingTextDto(parsingText));
  }

}
