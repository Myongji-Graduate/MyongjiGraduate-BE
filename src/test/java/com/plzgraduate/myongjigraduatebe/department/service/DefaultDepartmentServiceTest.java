package com.plzgraduate.myongjigraduatebe.department.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.repository.DepartmentRepository;
import com.plzgraduate.myongjigraduatebe.department.service.dto.AllDepartmentsResponse;
import com.plzgraduate.myongjigraduatebe.department.service.dto.DepartmentResponse;

@ExtendWith(MockitoExtension.class)
class DefaultDepartmentServiceTest {

  @Mock
  DepartmentRepository departmentRepository;

  @InjectMocks
  DefaultDepartmentService departmentService;

  private static final long departmentId1 = 1L;
  private static final long departmentId2 = 2L;

  private static final Department department1 = new Department("test1");
  private static final Department department2 = new Department("test2");

  private static final List<Department> departments = new ArrayList<>(2);

  @BeforeAll
  static void setUp() {
    ReflectionTestUtils.setField(department1, "id", departmentId1);
    ReflectionTestUtils.setField(department2, "id", departmentId2);
    departments.add(department1);
    departments.add(department2);
  }

  @Nested
  @DisplayName("findAll 메소드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("정상적으로 요청이 오면")
    class ContextWithCorrectRequest {

      @Test
      @DisplayName("학과 객체 전체 정보가 포함된 응답을 반환한다")
      void ItResponseAllDepartmentResponse() {
        // given
        given(departmentRepository.findAll()).willReturn(departments);

        // when
        AllDepartmentsResponse response = departmentService.findAll();

        // then
        long[] responseIds = response
            .getDepartments()
            .stream()
            .mapToLong(DepartmentResponse::getId)
            .toArray();
        long[] departmentsIds = departments
            .stream()
            .mapToLong(Department::getId)
            .toArray();

        assertThat(responseIds).isEqualTo(departmentsIds);
      }
    }
  }

}
