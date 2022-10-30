package com.plzgraduate.myongjigraduatebe.user.entity;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class StudentNumberTest {

  @Nested
  @DisplayName("of 메소드는")
  class DescribeOfMethod {

    @Nested
    @DisplayName("길이가 8이 아닌 문자열은")
    class ContextNotValidLength {

      @ParameterizedTest
      @ValueSource(strings = {"1234567", "숫자가아닌것들"})
      @NullAndEmptySource()
      @DisplayName("예외를 던진다")
      void ItThrowsIllegalArgumentException(String value) {
        // given
        // when
        // then
        assertThatThrownBy(() -> StudentNumber.of(value))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("길이가 8이지만 숫자가 아닌 문자열은")
    class ContextNotNumberStringWithValidLength {

      @ParameterizedTest
      @ValueSource(strings = {"영일이삼사오육칠팔"})
      @DisplayName("예외를 던진다.")
      void ItReturnInstance(String value) {
        // given
        // when
        // then
        assertThatThrownBy(() -> StudentNumber.of(value))
            .isInstanceOf(IllegalArgumentException.class);
      }
    }

    @Nested
    @DisplayName("8자리 숫자로된 문자열은")
    class ContextNumberStringWithValidLength {

      @ParameterizedTest
      @ValueSource(strings = {"60191667", "12345678"})
      @DisplayName("객체를 생선한다.")
      void ItReturnInstance(String value) {
        // given
        // when
        StudentNumber studentNumber = StudentNumber.of(value);

        // then
        assertThat(studentNumber).isNotNull();
      }
    }
  }

  @Nested
  @DisplayName("equals 메소드는")
  class DescribeEqualsMethod {

    @Nested
    @DisplayName("같은 value로 만들어진 객체는")
    class ContextInstanceFromSameValue {

      @ParameterizedTest
      @ValueSource(strings = {"60191667", "12345678"})
      @DisplayName("true를 반환한다")
      void ItReturnTrue(String value) {
        // given
        // when
        StudentNumber studentNumber1 = StudentNumber.of(value);
        StudentNumber studentNumber2 = StudentNumber.of(value);

        // then
        assertThat(studentNumber1.equals(studentNumber2)).isTrue();
      }
    }

    @Nested
    @DisplayName("서로 다른 value로 만들어진 객체는")
    class ContextInstanceFromDifferenceValue {

      @Test
      @DisplayName("false를 반환한다")
      void ItReturnFalse() {
        // given
        // when
        StudentNumber studentNumber1 = StudentNumber.of("60191667");
        StudentNumber studentNumber2 = StudentNumber.of("12345678");

        // then
        assertThat(studentNumber1.equals(studentNumber2)).isFalse();
      }
    }
  }

}
