package com.plzgraduate.myongjigraduatebe.auth.dto;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.plzgraduate.myongjigraduatebe.user.dto.Password;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;

import lombok.Getter;

@Getter
public class SignUpRequest {

  private final UserId userId;
  private final Password password;
  private final StudentNumber studentNumber;
  private final EnglishLevel engLv;

  @JsonCreator
  public SignUpRequest(
      @JsonProperty("userId")
      UserId userId,
      @JsonProperty("password")
      Password password,
      @JsonProperty("studentNumber")
      StudentNumber studentNumber,
      @JsonProperty("engLv")
      EnglishLevel engLv
  ) {
    this.userId = userId;
    this.password = password;
    this.studentNumber = studentNumber;
    this.engLv = engLv;
  }

  public User toEntity(PasswordEncoder passwordEncoder) {
    String encodedPw = passwordEncoder.encode(password.getValue());

    return new User(userId, encodedPw, studentNumber, engLv);
  }
}
