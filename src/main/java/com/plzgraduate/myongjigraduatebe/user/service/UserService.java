package com.plzgraduate.myongjigraduatebe.user.service;

import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

@Service
public interface UserService {

  UserIdValidityResponse checkValidityUserId(UserId userId);

  StudentNumberValidityResponse checkValidityStudentNumber(StudentNumber studentNumber);
}