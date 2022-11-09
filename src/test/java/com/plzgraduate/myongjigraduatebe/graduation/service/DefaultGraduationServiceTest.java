package com.plzgraduate.myongjigraduatebe.graduation.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.graduation.entity.GraduationRequirement;
import com.plzgraduate.myongjigraduatebe.graduation.repository.GraduationLectureRepository;
import com.plzgraduate.myongjigraduatebe.lecture.repository.LectureRepository;

@ExtendWith(MockitoExtension.class)
class DefaultGraduationServiceTest {

  @InjectMocks
  private DefaultGraduationService graduationService;

  @Mock
  private GraduationLectureRepository graduationLectureRepository;

  @Mock
  private DepartmentRepository departmentRepository;

  @Mock
  private LectureRepository lectureRepository;

  private static final long departmentId = 1L;
  private static final Department department = new Department("test1");

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

}
