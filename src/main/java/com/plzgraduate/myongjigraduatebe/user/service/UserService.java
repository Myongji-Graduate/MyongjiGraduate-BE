package com.plzgraduate.myongjigraduatebe.user.service;

import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentFindIdResponse;
import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentPageInfoResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

@Service
public interface UserService {

  UserIdValidityResponse checkValidityUserId(UserId userId);

  StudentNumberValidityResponse checkValidityStudentNumber(StudentNumber studentNumber);

  void saveStudentInfo(AuthenticatedUser user, ParsingTextDto parsingTextDto);

  StudentPageInfoResponse showStudentInfo(AuthenticatedUser user);

  StudentFindIdResponse findStudentId(StudentNumber studentNumber);

  void resetNewPassword(UserId userId, Password password);

  void deleteUser(AuthenticatedUser user, String password);

  void existUserByIdAndStudentNumber(UserId userId, StudentNumber studentNumber);
}
