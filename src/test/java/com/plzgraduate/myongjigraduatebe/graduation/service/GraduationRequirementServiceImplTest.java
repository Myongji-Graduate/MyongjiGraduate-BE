package com.plzgraduate.myongjigraduatebe.graduation.service;

import static com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequest;
import com.plzgraduate.myongjigraduatebe.graduation.dto.BachelorInfoRequirementResponse;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRequirementRepository;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

@ExtendWith(MockitoExtension.class)
class GraduationRequirementServiceImplTest {

  @InjectMocks
  private GraduationRequirementServiceImpl graduationRequirementService;

  @Mock
  private GraduationRequirementRepository graduationRequirementRepository;

  private final BachelorInfoRequest bachelorInfoRequest = new BachelorInfoRequest(19, "법학과");

  @Test
  @DisplayName("입학년도와 학과로 졸업요건학점을 조회한다.")
  void findByEntryYearAndDepartmentNameTest() {
    //given
    GraduationRequirement graduationRequirement = GraduationRequirement
        .builder()
        .commonCultureCredit(10)
        .coreCultureCredit(10)
        .department(new Department("법학과"))
        .engLv(EnglishLevel.ENG12)
        .freeElectiveCredit(10)
        .normalCultureCredit(10)
        .basicAcademicalCultureCredit(10)
        .totalCredit(60)
        .build();

    given(graduationRequirementRepository.findByNameAndEntryYear(EntryYear.of(bachelorInfoRequest.getEntryYear())
        , bachelorInfoRequest.getDepartment(), EnglishLevel.ENG12)).willReturn(graduationRequirement);

    //when
    BachelorInfoRequirementResponse bachelorInfoRequirementResponse = graduationRequirementService.getBachelorInfoRequirement(
        bachelorInfoRequest);

    //then
    assertThat(bachelorInfoRequirementResponse).isNotNull();
    assertThat(bachelorInfoRequirementResponse.getTotalCredit()).isEqualTo(
        graduationRequirement.getTotalCredit() + CHAPEL_CREDIT);
  }

}
