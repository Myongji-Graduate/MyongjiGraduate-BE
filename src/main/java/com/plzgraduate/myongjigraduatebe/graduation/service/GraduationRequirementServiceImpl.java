package com.plzgraduate.myongjigraduatebe.graduation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRequirementRepository;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraduationRequirementServiceImpl implements GraduationRequirementService{

  private final static EnglishLevel DEFAULT_ENGLISH_LV = EnglishLevel.ENG12;

  private final GraduationRequirementRepository graduationRequirementRepository;

  @Override
  public BachelorInfoRequirementResponse getBachelorInfoRequirement(BachelorInfoRequest bachelorInfoRequest) {
    GraduationRequirement foundGraduationRequirement = graduationRequirementRepository.findByNameAndEntryYear(
        bachelorInfoRequest.getEntryYear(),
        bachelorInfoRequest.getDepartment(),
        DEFAULT_ENGLISH_LV
    );
    return BachelorInfoRequirementResponse.from(foundGraduationRequirement);
  }
}
