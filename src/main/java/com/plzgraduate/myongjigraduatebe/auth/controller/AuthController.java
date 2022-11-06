package com.plzgraduate.myongjigraduatebe.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.auth.dto.SignUpRequest;
import com.plzgraduate.myongjigraduatebe.auth.service.JwtAuthService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final JwtAuthService authService;

  @PostMapping(value = "sign-up")
  @ResponseStatus(HttpStatus.CREATED)
  public void signUp(@RequestBody SignUpRequest userCreateReq) {
    authService.signUp(userCreateReq);
  }

}
