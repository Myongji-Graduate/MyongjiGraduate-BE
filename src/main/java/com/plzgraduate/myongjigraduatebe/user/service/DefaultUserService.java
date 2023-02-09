package com.plzgraduate.myongjigraduatebe.user.service;

import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentFindIdResponse;
import com.plzgraduate.myongjigraduatebe.user.repository.TakenLectureRepository;
import java.util.Optional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  private final PasswordEncoder passwordEncoder;
  private final TakenLectureRepository takenLectureRepository;

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
    if(parsingTextDto.getSplitText()[2].split(", ").length>5){
      throw new IllegalArgumentException("서비스 대상자가 맞는지 확인해 주세요. 서비스 오류일 경우 채널톡으로 문의 부탁드립니다.");
    }
    User user = userRepository
        .findUserById(authUser.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
    Department department = departmentRepository
        .findByName(parsingTextDto.getStudentDepartment())
        .orElseThrow(() -> new IllegalArgumentException("서비스 대상자가 맞는지 확인해 주세요. 서비스 오류일 경우 채널톡으로 문의 부탁드립니다."));
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

  @Override
  public StudentFindIdResponse findStudentId(StudentNumber studentNumber) {
    User user = userRepository
            .findUserByStudentNumber(studentNumber)
            .orElseThrow(() -> new IllegalArgumentException("해당 학번이 존재하지 않습니다."));
    return StudentFindIdResponse.of(user.getUserId().getId(),
            user.getStudentNumber().getValue()
    );
  }

  @Override
  public void deleteUser(AuthenticatedUser user, String password){
    if(!passwordEncoder.matches(password, user.getPassword())){
      throw new IllegalArgumentException("입력하신 비밀번호가 일치하지 않습니다.");
    }
    takenLectureRepository.deleteAllByUserId(user.getId());
    userRepository.deleteById(user.getId());
    SecurityContextHolder.clearContext();
  }

  @Override
  public void checkPasswordChangingUser(UserId userId, StudentNumber studentNumber) {
    Optional<User> byUserId = userRepository.findByUserId(userId);
    if(!userRepository.existsByUserId(userId)) {
      throw new IllegalArgumentException("해당 아이디의 사용자가 존재하지 않습니다.");
    }
    if(!userRepository.existsByStudentNumber(studentNumber)){
      throw new IllegalArgumentException("해당 학번의 사용자가 존재하지 않습니다.");
    }
    if(byUserId.isPresent() && !byUserId.get().getStudentNumber().equals(studentNumber)){
      throw new IllegalArgumentException("해당 아이디와 일치하는 학번이 없습니다.");
    }
  }

  @Override
  public void resetNewPassword(UserId userId, Password password){
    Optional<User> byUserId = userRepository.findByUserId(userId);
    byUserId.ifPresent(user -> user.updatePassword(passwordEncoder.encode(password.getValue())));
  }

}
