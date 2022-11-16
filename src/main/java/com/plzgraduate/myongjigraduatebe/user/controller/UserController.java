package com.plzgraduate.myongjigraduatebe.user.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.auth.CurrentUser;
import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.user.dto.EditedTakenLecture;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentPageInfoResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.TakenLectureResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserInitCheckResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.ParsingResult;
import com.plzgraduate.myongjigraduatebe.user.entity.RecodeParsingText;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.RecodeParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;
import com.plzgraduate.myongjigraduatebe.user.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users")
public class UserController {

  private final TakenLectureService takenLectureService;
  private final UserService userService;

  private final RecodeParsingTextRepository recodeRepository;

  @GetMapping("/me/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public TakenLectureResponse show(@CurrentUser AuthenticatedUser user) {
    return takenLectureService.showTakenLecture(user);
  }

  @PostMapping("/me/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public void save(
      @CurrentUser AuthenticatedUser user,
      @RequestBody HashMap<String, Object> param
  ) {
    String parsingText = param
        .get("parsingText")
        .toString();

    RecodeParsingText recode = new RecodeParsingText(user.getId(), parsingText);

    try {
      ParsingTextDto parsingTextDto = new ParsingTextDto(parsingText);
      takenLectureService.saveTakenLecture(user, parsingTextDto);
      userService.saveStudentInfo(user, parsingTextDto);

      recode.setParsingResult(ParsingResult.SUCCESS);
      recodeRepository.save(recode);

    } catch (Exception e) {

      recode.setParsingResult(ParsingResult.FAIL);
      recodeRepository.save(recode);

      throw new IllegalArgumentException(e);
    }
  }

  @PatchMapping("/me/taken-lectures")
  @ResponseStatus(HttpStatus.OK)
  public void edit(
      @CurrentUser AuthenticatedUser user,
      @RequestBody
      EditedTakenLecture editedTakenLecture
  ) {
    takenLectureService.editTakenLecture(
        user,
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

  @GetMapping("/me/init")
  @ResponseStatus(HttpStatus.OK)
  public UserInitCheckResponse checkInit(
      @CurrentUser AuthenticatedUser user
  ) {
    boolean isValidToken = user != null;
    boolean isInit = isValidToken && user.getName() != null;
    return new UserInitCheckResponse(isValidToken, isInit);
  }

  @GetMapping("/me/information")
  @ResponseStatus(HttpStatus.OK)
  public StudentPageInfoResponse showUserInfo(
      @CurrentUser AuthenticatedUser user
  ) {
    return userService.showStudentInfo(user);
  }

}
