package com.plzgraduate.myongjigraduatebe.department.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

  private static final int startedEntryYear = 15;

  private static final int endedEntryYear = 20;

  private static final Department department1 = new Department("test1", startedEntryYear, endedEntryYear);
  private static final Department department2 = new Department("test2", startedEntryYear, endedEntryYear);

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

  @Nested
  @DisplayName("isSupportedEntryYear 메소드는")
  class DescribeIsSupportedEntryYear {

    @Nested
    @DisplayName("학과 id에 해당하는 데이터가 없다면")
    class ContextWithNotExistsDepartmentId {

      @Test
      @DisplayName("예외를 발생시킨다.")
      void ItThrowsIllegalArgumentsException() {
        // given
        long departmentId = Long.MAX_VALUE - departmentId1;
        int entryYear = 19;
        given(departmentRepository.findById(departmentId)).willReturn(Optional.empty());

        // when
        // then
        assertThatThrownBy(() -> departmentService.isSupportedEntryYear(
            departmentId,
            entryYear
        )).isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("해당 학번을 지원하지 않는다면")
    class ContextWithNotSupportedEntryYear {

      @ParameterizedTest
      @ValueSource(ints = {startedEntryYear - 1, endedEntryYear + 1})
      @DisplayName("false를 반환합니다.")
      void ItReturnFalse(int entryYear) {
        // given
        given(departmentRepository.findById(departmentId1)).willReturn(Optional.of(department1));

        // when
        boolean response = departmentService.isSupportedEntryYear(
            departmentId1,
            entryYear
        );
        // then
        assertThat(response).isFalse();
      }
    }

    @Nested
    @DisplayName("해당 학번을 지원한다면")
    class ContextWithSupportedEntryYear {

      @ParameterizedTest
      @ValueSource(ints = {startedEntryYear, endedEntryYear, (startedEntryYear + endedEntryYear) / 2})
      @DisplayName("true를 반환합니다.")
      void ItReturnTrue(int entryYear) {
        // given
        given(departmentRepository.findById(departmentId1)).willReturn(Optional.of(department1));

        // when
        boolean response = departmentService.isSupportedEntryYear(
            departmentId1,
            entryYear
        );
        // then
        assertThat(response).isTrue();
      }
    }
  }

}
