package com.plzgraduate.myongjigraduatebe.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.plzgraduate.myongjigraduatebe.common.ControllerSetUp;
import com.plzgraduate.myongjigraduatebe.common.WithMockUserIsInitialized;
import com.plzgraduate.myongjigraduatebe.common.WithMockUserIsNotInitialized;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentNumberValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.RecodeParsingTextRepository;
import com.plzgraduate.myongjigraduatebe.user.service.TakenLectureService;
import com.plzgraduate.myongjigraduatebe.user.service.UserService;

@WebMvcTest(UserController.class)
class UserControllerTest extends ControllerSetUp {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private TakenLectureService takenLectureServiceo;

  @MockBean
  private RecodeParsingTextRepository recodeRepository;

  private static final String BASE_URL = "/api/v1/users";

  private static final long departmentId = 1L;

  private static final Department department = new Department("testDepartment");

  private static final long id = 1L;
  private static final String userId = "userId";
  private static final String password = "testpassword";
  private static final String studentNumber = "60191667";
  private static final EnglishLevel engLv = EnglishLevel.ENG34;
  private static final User user = new User(UserId.valueOf(userId), password, StudentNumber.valueOf(studentNumber), engLv);

  @BeforeAll
  static void setUp() {
    ReflectionTestUtils.setField(department, "id", departmentId);
    ReflectionTestUtils.setField(user, "id", id);
    ReflectionTestUtils.setField(user, "department", department);
  }

  @Nested
  @DisplayName("CheckValidityUserId ????????????")
  class DescribeCheckValidityUserId {

    private final String PATH = "/userid-validity-checks";

    @Nested
    @DisplayName("?????? ?????? UserId??????")
    class ContextWithExistUserId {

      @Test
      @DisplayName("???????????? ???????????? ?????? false??? ????????????")
      void ItResponseWithIsNotDuplicatedValueOfFalse() throws Exception {

        //given
        UserId existUserId = UserId.valueOf(userId);
        given(userService.checkValidityUserId(existUserId)).willReturn(new UserIdValidityResponse(existUserId, true));

        String requestBody = objectMapper.writeValueAsString(existUserId);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL + PATH)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "check duplicated userid",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("userId")
                        .type(JsonFieldType.STRING)
                        .description("???????????? ????????? ???????????????"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("????????? ?????? ??? ??????")
                )
            ));

      }
    }

    @Nested
    @DisplayName("???????????? ?????? UserId??????")
    class ContextWithNotExistUserId {

      @Test
      @DisplayName("???????????? ???????????? ?????? true??? ????????????")
      void ItResponseWithIsNotDuplicatedValueOfTrue() throws Exception {

        //given
        UserId notExistUserId = UserId.valueOf(userId);
        given(userService.checkValidityUserId(notExistUserId)).willReturn(new UserIdValidityResponse(notExistUserId, false));

        String requestBody = objectMapper.writeValueAsString(notExistUserId);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL + PATH)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "check duplicated userid",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("userId")
                        .type(JsonFieldType.STRING)
                        .description("???????????? ????????? ???????????????"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("????????? ?????? ??? ??????")
                )
            ));

      }
    }
  }

  @Nested
  @DisplayName("CheckValidityStudentNumber ????????????")
  class DescribeCheckValidityStudentNumber {

    private final String PATH = "/studentNumber-validity-checks";

    @Nested
    @DisplayName("?????? ?????? ???????????????")
    class ContextWithExistStudentNumber {

      @Test
      @DisplayName("???????????? ???????????? ?????? false??? ????????????")
      void ItResponseWithIsNotDuplicatedValueOfFalse() throws Exception {

        //given
        StudentNumber existStudentNumber = StudentNumber.valueOf(studentNumber);
        given(userService.checkValidityStudentNumber(existStudentNumber)).willReturn(new StudentNumberValidityResponse(
            existStudentNumber,
            true
        ));

        String requestBody = objectMapper.writeValueAsString(existStudentNumber);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL + PATH)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "check duplicated student number",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("studentNumber")
                        .type(JsonFieldType.STRING)
                        .description("???????????? ????????? ??????"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("???????????? ???????????? true, ??????????????? false??? ??????")
                )
            ));

      }
    }

    @Nested
    @DisplayName("???????????? ?????? ???????????????")
    class ContextWithNotExistStudentNumber {

      @Test
      @DisplayName("???????????? ???????????? ?????? true??? ????????????")
      void ItResponseWithIsNotDuplicatedValueOfTrue() throws Exception {

        //given
        StudentNumber notExistStudentNumber = StudentNumber.valueOf(studentNumber);
        given(userService.checkValidityStudentNumber(notExistStudentNumber)).willReturn(new StudentNumberValidityResponse(
            notExistStudentNumber,
            false
        ));

        String requestBody = objectMapper.writeValueAsString(notExistStudentNumber);

        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders
                                                     .post(BASE_URL + PATH)
                                                     .contentType(MediaType.APPLICATION_JSON)
                                                     .content(requestBody));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "check duplicated student number",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("studentNumber")
                        .type(JsonFieldType.STRING)
                        .description("???????????? ????????? ??????"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("???????????? ???????????? true, ??????????????? false??? ??????")
                )
            ));

      }
    }
  }

  @Nested
  @DisplayName("checkInit ????????????")
  class DescribeCheckInit {

    private final String PATH = "/me/init";

    @Nested
    @DisplayName("????????? ???????????? ????????????")
    class ContextWithTokenIsInvalid {

      @Test
      @DisplayName("?????? ????????? ???????????? ?????? false??? ????????????")
      void ItResponseWithValidTokenAndInitValueFalse() throws Exception {

        //given
        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + PATH));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "is invalid token",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("validToken")
                        .type(JsonFieldType.BOOLEAN)
                        .description("?????????????????? true, ???????????? false??? ??????"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("????????????????????? ture, ???????????? false??? ??????")
                )
            ));

      }
    }

    @Nested
    @WithMockUserIsInitialized
    @DisplayName("???????????? ?????? ????????? ???????????????")
    class ContextWithUserIsInitialized {

      @Test
      @DisplayName("init??? ?????? true??? ????????????")
      void ItResponseWithInitValueTrue() throws Exception {

        //given
        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + PATH));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "is initialization for user",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("validToken")
                        .type(JsonFieldType.BOOLEAN)
                        .description("?????????????????? true, ???????????? false??? ??????"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("????????????????????? ture, ???????????? false??? ??????")
                )
            ));

      }
    }

    @Nested
    @WithMockUserIsNotInitialized
    @DisplayName("???????????? ?????? ?????? ???????????????")
    class ContextWithUserIsNotInitialized {

      @Test
      @DisplayName("init??? ?????? false??? ????????????")
      void ItResponseWithInitValueFalse() throws Exception {

        //given
        // when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + PATH));

        // then
        response
            .andExpect(status().isOk())
            .andDo(document(
                "is initialization for user",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("validToken")
                        .type(JsonFieldType.BOOLEAN)
                        .description("?????????????????? true, ???????????? false??? ??????"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("????????????????????? ture, ???????????? false??? ??????")
                )
            ));

      }
    }

  }

}
