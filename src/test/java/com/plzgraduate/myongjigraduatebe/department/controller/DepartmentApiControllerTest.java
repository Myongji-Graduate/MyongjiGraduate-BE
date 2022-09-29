package com.plzgraduate.myongjigraduatebe.department.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import com.plzgraduate.myongjigraduatebe.common.ControllerSetUp;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.department.service.DepartmentService;
import com.plzgraduate.myongjigraduatebe.department.service.dto.AllDepartmentsResponse;

@WebMvcTest(controllers = DepartmentApiController.class)
class DepartmentApiControllerTest extends ControllerSetUp {

  @MockBean
  private DepartmentService departmentService;

  private static final String BASE_URL = "/api/v1/departments";

  private static final long departmentId1 = 1L;
  private static final long departmentId2 = 2L;

  private static final Department department1 = new Department("test1");
  private static final Department department2 = new Department("test2");

  private static AllDepartmentsResponse allDepartmentsResponse;

  @BeforeAll
  static void setUp() {
    ReflectionTestUtils.setField(department1, "id", departmentId1);
    ReflectionTestUtils.setField(department2, "id", departmentId2);
    allDepartmentsResponse = AllDepartmentsResponse.from(List.of(department1, department2));
  }

  @Nested
  @DisplayName("findAll 메소드는")
  class DescribeFindAll {

    @Nested
    @DisplayName("정상적인 요청이 오면")
    class ContextWithCorrectRequest {

      @Test
      @DisplayName("학과 객체 전체 정보가 포함된 응답을 반환한다")
      void ItResponseAllDepartment() throws Exception {
        // given
        given(departmentService.findAll()).willReturn(allDepartmentsResponse);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
              "findAll Departments",
              preprocessRequest(prettyPrint()),
              preprocessResponse(prettyPrint()),
              responseFields(
                fieldWithPath("departments[]").type(JsonFieldType.ARRAY).description("학과 정보 배열"),
                fieldWithPath("departments[].id").type(JsonFieldType.NUMBER).description("학과 식별자"),
                fieldWithPath("departments[].name").type(JsonFieldType.STRING).description("학과명")
              )
            ));
      }
    }
  }
}
