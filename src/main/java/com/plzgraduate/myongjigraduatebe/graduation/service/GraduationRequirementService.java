package com.plzgraduate.myongjigraduatebe.graduation.service;

import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoLecturesResponse;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;

public interface GraduationRequirementService {

  BachelorInfoRequirementResponse getBachelorInfoRequirement(BachelorInfoRequest bachelorInfoRequest);

  BachelorInfoLecturesResponse getBachelorInfoLectures(BachelorInfoRequest bachelorInfoRequest);
}