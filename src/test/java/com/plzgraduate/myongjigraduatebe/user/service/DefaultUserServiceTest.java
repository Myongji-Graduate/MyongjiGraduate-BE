package com.plzgraduate.myongjigraduatebe.user.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

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
import com.plzgraduate.myongjigraduatebe.user.dto.UserIdValidityResponse;
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;
import com.plzgraduate.myongjigraduatebe.user.entity.StudentNumber;
import com.plzgraduate.myongjigraduatebe.user.entity.User;
import com.plzgraduate.myongjigraduatebe.user.entity.UserId;
import com.plzgraduate.myongjigraduatebe.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class DefaultUserServiceTest {

  @InjectMocks
  private DefaultUserService defaultUserService;

  @Mock
  private UserRepository userRepository;

  private static final long departmentId = 1L;

  private static final Department department = new Department("testDepartment");

  private static final long id = 1L;
  private static final String userId = "userId";
  private static final String password = "testpassword";
  private static final String studentNumber = "60191667";
  private static final EnglishLevel engLv = EnglishLevel.MIDDLE;
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

    @Nested
    @DisplayName("이미 있는 UserId라면")
    class ContextWithExistUserId {

      @Test
      @DisplayName("NotDuplicated이 False인 응답을 반환한다.")
      void ItReturnResponseWithNotDuplicatedOfFalse() {

        //given
        UserId existUserId = UserId.valueOf(userId);
        given(userRepository.existsByUserId(existUserId)).willReturn(true);

        // when
        UserIdValidityResponse response = defaultUserService.checkValidityUserId(existUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(existUserId);
        assertThat(response.getIsNotDuplicated()).isFalse();

      }
    }

    @Nested
    @DisplayName("존재하지 않는 UserId라면")
    class ContextWithNotExistUserId {

      @Test
      @DisplayName("NotDuplicated이 true 인 응답을 반환한다.")
      void ItReturnResponseWithNotDuplicatedOfTrue() {

        //given
        UserId notExistUserId = UserId.valueOf(userId + 1);
        given(userRepository.existsByUserId(notExistUserId)).willReturn(false);

        // when
        UserIdValidityResponse response = defaultUserService.checkValidityUserId(notExistUserId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(notExistUserId);
        assertThat(response.getIsNotDuplicated()).isTrue();
      }
    }
  }

}
