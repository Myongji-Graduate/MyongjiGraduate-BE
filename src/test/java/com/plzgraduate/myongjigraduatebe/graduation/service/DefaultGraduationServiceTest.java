package com.plzgraduate.myongjigraduatebe.graduation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.plzgraduate.myongjigraduatebe.common.utils.PdfParser;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationRepository;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;

@ExtendWith(MockitoExtension.class)
class DefaultGraduationServiceTest {

  @InjectMocks
  private DefaultGraduationService graduationService;

  @Mock
  private GraduationRepository graduationRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private LectureRepository lectureRepository;

  private static final long departmentId = 1L;
  private static final int startedEntryYear = 15;
  private static final int endedEntryYear = 20;
  private static final Department department = new Department("test1", startedEntryYear, endedEntryYear);

  private static final int totalCredit = 20;
  private static final int majorCredit = 20;
  private static final int entryYear = 19;
  private static final int commonCultureCredit = 3;
  private static final int coreCultureCredit = 3;
  private static final int basicAcademicalCultureCredit = 18;
  private static final int normalCultureCredit = 9;
  private static final int freeElectiveCredit = 7;
  private static final GraduationRequirement requirement = GraduationRequirement
      .builder()
      .totalCredit(totalCredit)
      .majorCredit(majorCredit)
      .entryYear(entryYear)
      .commonCultureCredit(commonCultureCredit)
      .coreCultureCredit(coreCultureCredit)
      .basicAcademicalCultureCredit(basicAcademicalCultureCredit)
      .normalCultureCredit(normalCultureCredit)
      .freeElectiveCredit(freeElectiveCredit)
      .build();


  @BeforeAll
  static void setUp() {
    ReflectionTestUtils.setField(department, "id", departmentId);
  }

  @Nested
  @DisplayName("assess 함수는")
  class DescribeAssessMethod {

    @Nested
    @DisplayName("pdf parsing 문자열이 올바르지 않다면")
    class ContextWithNotValidPdf {

      @Test
      @DisplayName("예외를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        String parsingText = "invalid";

        // when
        // then
        assertThatThrownBy(() -> graduationService.assess(parsingText))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("학과 정보가 존재하지 않으면")
    class ContextWithNotExistDepartment {

      @Test
      @DisplayName("예외를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        String parsingText = PdfParser.getSampleString();
        given(departmentRepository.findByName(anyString())).willThrow(IllegalArgumentException.class);

        // when
        // then
        assertThatThrownBy(() -> graduationService.assess(parsingText))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("입학 년도 정보가 존재하지 않으면")
    class ContextWithNotExistEntryYear {

      @Test
      @DisplayName("졸업 요건 조회 시 예외를 발생시킨다.")
      void ItThrowsIllegalArgumentException() {
        // given
        String parsingText = PdfParser.getSampleString();
        given(departmentRepository.findByName(anyString())).willReturn(Optional.of(department));
        given(graduationRepository.findRequirementByDepartmentAndEntryYear(any(), anyInt()))
            .willThrow(IllegalArgumentException.class);

        // when
        // then
        assertThatThrownBy(() -> graduationService.assess(parsingText))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("졸업 요건 정보가 존재하지 않으면")
    class ContextWithNotExistRequirement {

      @Test
      @DisplayName("졸업 과목을 조회할 수 없다")
      void ItThrowsIllegalArgumentException() {
        // given
        // String parsingText = PdfParser.getSampleString();
        // given(departmentRepository.findByName(anyString())).willReturn(Optional.of(department));
        // given(graduationRepository.findRequirementByDepartmentAndEntryYear(any(), anyInt()))
        //     .willReturn(Optional.empty());

        Optional<GraduationRequirement> opRequirement = Optional.of(null);

        // when

        // then
       assertThat(graduationRepository.findAllGraduationLecture(opRequirement.get())).isEqualTo(Optional.empty());
      }
    }
  }
}
