package com.plzgraduate.myongjigraduatebe.user.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.plzgraduate.myongjigraduatebe.auth.dto.AuthenticatedUser;
import com.plzgraduate.myongjigraduatebe.user.dto.PasswordResetRequest;
import com.plzgraduate.myongjigraduatebe.user.dto.StudentFindIdResponse;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;
import com.plzgraduate.myongjigraduatebe.user.validator.PasswordResetRequestValidator;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(UserController.class)
@Import(PasswordResetRequestValidator.class)
class UserControllerTest extends ControllerSetUp {

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @MockBean
  private TakenLectureService takenLectureService;

  @MockBean
  private RecodeParsingTextRepository recodeRepository;

  private static final String BASE_URL = "/api/v1/users";

  private static final long departmentId = 1L;

  private static final Department department = new Department("testDepartment");

  private static final long id = 1L;
  private static final String userId = "userId";
  private static final String password = "testpassword!";
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
  @DisplayName("CheckValidityUserId 메서드는")
  class DescribeCheckValidityUserId {

    private final String PATH = "/userid-validity-checks";

    @Nested
    @DisplayName("이미 있는 UserId라면")
    class ContextWithExistUserId {

      @Test
      @DisplayName("중복되지 않았다는 값을 false로 응답한다")
      void ItResponseWithIsNotDuplicatedValueOfFalse() throws Exception {

        //given
        UserId existUserId = UserId.valueOf(userId);
        given(userService.checkValidityUserId(existUserId)).willReturn(new UserIdValidityResponse(existUserId, true));

        String requestBody = objectMapper.writeValueAsString(existUserId);

        // when
        ResultActions response = mockMvc.perform(post(BASE_URL + PATH)
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
                        .description("사용자가 입력한 유저아이디"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("중복이 아닌 지 여부")
                )
            ));

      }
    }

    @Nested
    @DisplayName("존재하지 않는 UserId라면")
    class ContextWithNotExistUserId {

      @Test
      @DisplayName("중복되지 않았다는 값을 true로 응답한다")
      void ItResponseWithIsNotDuplicatedValueOfTrue() throws Exception {

        //given
        UserId notExistUserId = UserId.valueOf(userId);
        given(userService.checkValidityUserId(notExistUserId)).willReturn(new UserIdValidityResponse(notExistUserId, false));

        String requestBody = objectMapper.writeValueAsString(notExistUserId);

        // when
        ResultActions response = mockMvc.perform(post(BASE_URL + PATH)
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
                        .description("사용자가 입력한 유저아이디"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("중복이 아닌 지 여부")
                )
            ));

      }
    }
  }

  @Nested
  @DisplayName("CheckValidityStudentNumber 메서드는")
  class DescribeCheckValidityStudentNumber {

    private final String PATH = "/studentNumber-validity-checks";

    @Nested
    @DisplayName("이미 있는 학번이라면")
    class ContextWithExistStudentNumber {

      @Test
      @DisplayName("중복되지 않았다는 값을 false로 응답한다")
      void ItResponseWithIsNotDuplicatedValueOfFalse() throws Exception {

        //given
        StudentNumber existStudentNumber = StudentNumber.valueOf(studentNumber);
        given(userService.checkValidityStudentNumber(existStudentNumber)).willReturn(new StudentNumberValidityResponse(
            existStudentNumber,
            true
        ));

        String requestBody = objectMapper.writeValueAsString(existStudentNumber);

        // when
        ResultActions response = mockMvc.perform(post(BASE_URL + PATH)
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
                        .description("사용자가 입력한 학번"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("중복되지 않았다면 true, 중복이라면 false를 반환")
                )
            ));

      }
    }

    @Nested
    @DisplayName("존재하지 않는 학번이라면")
    class ContextWithNotExistStudentNumber {

      @Test
      @DisplayName("중복되지 않았다는 값을 true로 응답한다")
      void ItResponseWithIsNotDuplicatedValueOfTrue() throws Exception {

        //given
        StudentNumber notExistStudentNumber = StudentNumber.valueOf(studentNumber);
        given(userService.checkValidityStudentNumber(notExistStudentNumber)).willReturn(new StudentNumberValidityResponse(
            notExistStudentNumber,
            false
        ));

        String requestBody = objectMapper.writeValueAsString(notExistStudentNumber);

        // when
        ResultActions response = mockMvc.perform(post(BASE_URL + PATH)
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
                        .description("사용자가 입력한 학번"),
                    fieldWithPath("isNotDuplicated")
                        .type(JsonFieldType.BOOLEAN)
                        .description("중복되지 않았다면 true, 중복이라면 false를 반환")
                )
            ));

      }
    }
  }

  @Nested
  @DisplayName("checkInit 메서드는")
  class DescribeCheckInit {

    private final String PATH = "/me/init";

    @Nested
    @DisplayName("토큰이 인증되지 않았다며")
    class ContextWithTokenIsInvalid {

      @Test
      @DisplayName("토큰 인증과 초기화의 값을 false로 응답한다")
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
                        .description("인증되었다면 true, 아니라면 false를 반환"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("초기화되었다면 ture, 아니라면 false를 반환")
                )
            ));

      }
    }

    @Nested
    @WithMockUserIsInitialized
    @DisplayName("초기화를 이미 진행한 사용자라면")
    class ContextWithUserIsInitialized {

      @Test
      @DisplayName("init의 값을 true로 응답한다")
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
                        .description("인증되었다면 true, 아니라면 false를 반환"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("초기화되었다면 ture, 아니라면 false를 반환")
                )
            ));

      }
    }

    @Nested
    @WithMockUserIsNotInitialized
    @DisplayName("초기화를 하지 않은 사용자라면")
    class ContextWithUserIsNotInitialized {

      @Test
      @DisplayName("init의 값을 false로 응답한다")
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
                        .description("인증되었다면 true, 아니라면 false를 반환"),
                    fieldWithPath("init")
                        .type(JsonFieldType.BOOLEAN)
                        .description("초기화되었다면 ture, 아니라면 false를 반환")
                )
            ));

      }
    }

  }

  @Nested
  @DisplayName("showStudentId 메서드는")
  class DescribeShowStudent {
    private final String PATH = "/by/student-number/{studentNumber}";
    @Nested
    @DisplayName("해당 학번이 존재할 경우")
    class ContextWithExistsStudentNumber {
      @Test
      @DisplayName("해당 id를 반환한다.")
      void ItResponseWithUserId() throws Exception {
        //given
        given(userService.findStudentId(any(StudentNumber.class))).willReturn(
                StudentFindIdResponse.of(userId, studentNumber));
        //when
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get(BASE_URL + PATH, user.getStudentNumber().getValue()));
        //then
        response.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
      }

    }

    @Nested
    @DisplayName("해당 학번이 존재하지 않을 경우")
    class ContextWithNotExistsStudentNumber {
      @Test
      @DisplayName("BadRequest를 응답한다.")
      void ItThrowsBadRequest() throws Exception {
        //given
        String notExistStudentNumber = "60181666";
        given(userService.findStudentId(any(StudentNumber.class))).willThrow(IllegalArgumentException.class);
        //when
        ResultActions response = mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL + PATH, notExistStudentNumber));
        //then
        response
                .andDo(print())
                .andExpect(status().isBadRequest());
      }

    }
  }

  @Nested
  @DisplayName("resetPassword메서드는")
  class DescribeResetPassword {

    private final String PATH = "/reset-pw";

    @Nested
    @DisplayName("올바른 passwordResetRequest가 요청될경우")
    class ContextWithRightPasswordResetRequest{
      final String newPassword = "testPassword!!";
      @Test
      @DisplayName("성공 후 OK를 반환된다.")
      void ItReturns200Ok() throws Exception {
        //given
        PasswordResetRequest requestDto = new PasswordResetRequest(userId, newPassword, newPassword);
        String requestBody = objectMapper.writeValueAsString(requestDto);
        given(userRepository.existsByUserId(any(UserId.class))).willReturn(true);
        given(userRepository.existsByStudentNumber(any(StudentNumber.class))).willReturn(true);

        //when
        MockHttpServletRequestBuilder request = post(BASE_URL + PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andDo(print())
                .andExpect(status().isOk());
      }
    }

    @Nested
    @DisplayName("비밀번호 확인이 일치하지 않을 경우")
    class ContextWithInvalidPasswordCheck {
      String notMatingPasswordCheck = "testpassword@@";
      @Test
      @DisplayName("BadRequest를 반환한다.")
      void ItReturnsBadRequest() throws Exception {
        //given
        PasswordResetRequest requestDto = new PasswordResetRequest(userId, password, notMatingPasswordCheck);
        String requestBody = objectMapper.writeValueAsString(requestDto);

        //when

        MockHttpServletRequestBuilder request = post(BASE_URL + PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andDo(print())
                .andExpect(status().isBadRequest());
      }
    }

    @Nested
    @DisplayName("해당 아이디의 사용자를 찾을 수 없는 경우")
    class ContextWithNotExistsUserId {
      final String notExistUserId = "testingid";
      @Test
      @DisplayName("BadRequest를 반환한다.")
      void ItReturnsBadRequest() throws Exception {
        //given
        PasswordResetRequest requestDto = new PasswordResetRequest(notExistUserId, password, password);
        String requestBody = objectMapper.writeValueAsString(requestDto);
        given(userRepository.existsByUserId(any(UserId.class))).willReturn(false);

        //when
        MockHttpServletRequestBuilder request = post(BASE_URL + PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        ResultActions response = mockMvc.perform(request);

        //then
        response.andDo(print())
                .andExpect(status().isBadRequest());
      }
    }
  }

  @Nested
  @DisplayName("deleteUser메서드를 실행하면")
  class DescribeDeleteUser {
    private final String PATH = "/leave";
    @Nested
    @DisplayName("올바른 비밀번호를 입력받을 경우")
    class ContextWithRightPassword {
      @Test
      @WithMockUserIsInitialized
      @DisplayName("회원 탈퇴 성공")
      void ItReturns200OK() throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("password", "wrong");
        String requestBody = objectMapper.writeValueAsString(map);
        MockHttpServletRequestBuilder request = post(BASE_URL + PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        ResultActions response = mockMvc.perform(request);
        response.andDo(print())
                .andExpect(status().isOk());
      }
    }

    @Nested
    @DisplayName("잘못된 비밀번호를 입력받을 경우")
    class ContextWithWrongPassword {
      @Test
      @WithMockUserIsInitialized
      @DisplayName("회원 탈퇴 실패")
      void ItReturnsBadRequest() throws Exception {
        doThrow(new IllegalArgumentException()).when(userService)
                .deleteUser(any(AuthenticatedUser.class), anyString());
        Map<String, String> map = new HashMap<>();
        map.put("password", "wrong");
        String requestBody = objectMapper.writeValueAsString(map);
        MockHttpServletRequestBuilder request = post(BASE_URL + PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody);

        ResultActions response = mockMvc.perform(request);
        response.andDo(print())
                .andExpect(status().isBadRequest());

      }
    }
  }
}
