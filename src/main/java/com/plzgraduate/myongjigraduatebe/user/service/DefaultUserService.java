package com.plzgraduate.myongjigraduatebe.user.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.user.dto.ParsingTextDto;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentPageInfoResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

  private final UserRepository userRepository;
  private final DepartmentRepository departmentRepository;

  @Override
  public UserIdValidityResponse checkValidityUserId(UserId userId) {
    boolean isDuplicated = userRepository.existsByUserId(userId);
    return new UserIdValidityResponse(userId, isDuplicated);
  }

  @Override
  public StudentNumberValidityResponse checkValidityStudentNumber(StudentNumber studentNumber) {
    boolean isDuplicated = userRepository.existsByStudentNumber(studentNumber);
    return new StudentNumberValidityResponse(studentNumber, isDuplicated);
  }

  @Override
  public void saveStudentInfo(
      AuthenticatedUser authUser,
      ParsingTextDto parsingTextDto
  ) {
    User user = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    Department department = departmentRepository
        .findByName(parsingTextDto.getStudentDepartment())
        .orElseThrow(() -> new IllegalArgumentException("해당 학과가 없습니다."));
    user.updateStudentInfo(parsingTextDto.getStudentName(), department);
  }

  @Override
  public StudentPageInfoResponse showStudentInfo(AuthenticatedUser user) {

    return StudentPageInfoResponse
        .builder()
        .studentNumber(user.getStudentNumber().getValue())
        .studentName(user.getName())
        .department(user.getDepartment().getName())
        .build();
  }

}
