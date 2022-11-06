package com.plzgraduate.myongjigraduatebe.auth.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.plzgraduate.myongjigraduatebe.auth.dto.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtAuthService implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public void signUp(SignUpRequest signUpReq) {
    User newUser = signUpReq.toEntity(passwordEncoder);

    if (userRepository.existsByUserIdOrStudentNumber(newUser.getUserId(), newUser.getStudentNumber())) {
      throw new IllegalArgumentException("이미 가입된 학번 또는 아이디입니다.");
    }

    userRepository.save(newUser);
  }
}
