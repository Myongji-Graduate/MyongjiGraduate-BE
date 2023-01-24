package com.plzgraduate.myongjigraduatebe.graduation.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.service.GraduationRequirementService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/bachelor-info")
public class GraduationRequirementApiController {

  private final GraduationRequirementService graduationRequirementService;

  @GetMapping("/requirement")
  @ResponseStatus(HttpStatus.OK)
  public BachelorInfoRequirementResponse getBachelorInfoRequirement(
      @RequestBody
      BachelorInfoRequest bachelorInfoRequest
  ) {
    return graduationRequirementService.find(bachelorInfoRequest);
  }
}
