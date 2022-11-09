package com.plzgraduate.myongjigraduatebe.graduation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.auth.CurrentUser;
import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.graduation.dto.GraduationResult;
import com.plzgraduate.myongjigraduatebe.graduation.service.GraduationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/graduation")
public class GraduationApiController {

  private final GraduationService graduationService;

  @GetMapping("/result")
  @ResponseStatus(HttpStatus.OK)
  public GraduationResult getResult(
      @CurrentUser
      AuthenticatedUser user
  ) {
    return graduationService.getResult(user);
  }
}
