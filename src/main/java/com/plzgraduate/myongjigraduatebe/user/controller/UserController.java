package com.plzgraduate.myongjigraduatebe.user.controller;

import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.dto.PasswordCheckRequest;
import com.plzgraduate.myongjigraduatebe.user.validator.PasswordResetRequestValidator;
import com.plzgraduate.myongjigraduatebe.user.dto.PasswordResetRequest;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentFindIdResponse;
import java.util.HashMap;

import java.util.Map;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final PasswordResetRequestValidator passwordResetRequestValidator;

  @InitBinder("passwordResetRequest")
  public void passwordResetRequestInitBinder(WebDataBinder webDataBinder){
    webDataBinder.addValidators(passwordResetRequestValidator);
  }

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
      userService.saveStudentInfo(user, parsingTextDto);
      takenLectureService.saveTakenLecture(user, parsingTextDto);

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

  @GetMapping("/by/student-number/{studentNumber}")
  @ResponseStatus(HttpStatus.OK)
  public StudentFindIdResponse showUserId(@PathVariable String studentNumber){
    return userService.findStudentId(StudentNumber.valueOf(studentNumber));
  }

  @PostMapping ("/pwinquiry")
  @ResponseStatus(HttpStatus.OK)
  public void checkPasswordChangingUser(@RequestBody PasswordCheckRequest passwordCheckRequest) {
    userService.checkPasswordChangingUser(
            UserId.valueOf(passwordCheckRequest.getUserId()),
            StudentNumber.valueOf(passwordCheckRequest.getStudentNumber()));
  }

  @PostMapping("/reset-pw")
  @ResponseStatus(HttpStatus.OK)
  public void resetPassword(@RequestBody @Valid PasswordResetRequest passwordResetRequest){
    userService.resetNewPassword(
            UserId.valueOf(passwordResetRequest.getUserId()),
            Password.valueOf(passwordResetRequest.getNewPassword()));
  }

  @PostMapping("/leave")
  @ResponseStatus(HttpStatus.OK)
  public void deleteUser(@CurrentUser AuthenticatedUser user, @RequestBody Map<String ,String> passwordMap) {
    userService.deleteUser(user, passwordMap.get("password"));
  }

}
