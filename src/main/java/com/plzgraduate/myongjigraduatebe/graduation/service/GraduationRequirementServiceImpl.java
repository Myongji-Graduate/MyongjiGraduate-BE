package com.plzgraduate.myongjigraduatebe.graduation.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRequirementRepository;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GraduationRequirementServiceImpl implements GraduationRequirementService{

  private final GraduationRequirementRepository graduationRequirementRepository;

  @Override
  @Transactional(readOnly = true)
  public BachelorInfoRequirementResponse find(BachelorInfoRequest bachelorInfoRequest) {
    GraduationRequirement findGraduationRequirement = graduationRequirementRepository.findByNameAndEntryYear(
        EntryYear.of(bachelorInfoRequest.getEntryYear()),
        bachelorInfoRequest.getDepartment(),
        EnglishLevel.ENG12
    );
    return BachelorInfoRequirementResponse.from(findGraduationRequirement);
  }
}
