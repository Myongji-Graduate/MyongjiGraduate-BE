package com.plzgraduate.myongjigraduatebe.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.user.dto.EditedTakenLecture;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;
import com.plzgraduate.myongjigraduatebe.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

  private final TakenLectureService takenLectureService;
  private final UserService userService;

  @GetMapping("/{id}/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public TakenLectureResponse show(@PathVariable("id") long id) {
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

  @PatchMapping("/{id}/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public void edit(
      @PathVariable("id") long id,
      @RequestBody
      EditedTakenLecture editedTakenLecture
  ) {
    takenLectureService.editTakenLecture(
        id,
        editedTakenLecture
    );
  }

  @PostMapping("/userid-validity-checks")
  @ResponseStatus(HttpStatus.OK)
  public UserIdValidityResponse checkValidityUserId(
      @RequestBody UserId userId
  ) {
    return userService.checkValidityUserId(userId);
  }

  @PostMapping("/studentNumber-validity-checks")
  @ResponseStatus(HttpStatus.OK)
  public StudentNumberValidityResponse checkValidityStudentNumber(
      @RequestBody StudentNumber studentNumber
  ) {
    return userService.checkValidityStudentNumber(studentNumber);
  }

}
