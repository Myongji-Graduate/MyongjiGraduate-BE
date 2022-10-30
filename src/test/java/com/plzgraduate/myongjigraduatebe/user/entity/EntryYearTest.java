package com.plzgraduate.myongjigraduatebe.user.entity;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

class EntryYearTest {

  private static final int MIN_VALUE;
  private static final int MAX_VALUE;

  static {
    try {
      Field minValueField = EntryYear.class.getDeclaredField("MIN_VALUE");
      minValueField.setAccessible(true);
      MIN_VALUE = minValueField.getInt(null);
      Field maxValueField = EntryYear.class.getDeclaredField("MAX_VALUE");
      maxValueField.setAccessible(true);
      MAX_VALUE = maxValueField.getInt(null);
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new RuntimeException(e);
    }
  }

  @Nested
  @DisplayName("of 메소드는")
  class DescribeOfMethod {

    @Nested
    @DisplayName("범위 밖에 인트 값에 대해서")
    class ContextOutOfRange {

      @ParameterizedTest
      @ArgumentsSource(OutOfRangeValueArgumentsProvider.class)
      @DisplayName("예외를 던진다")
      void ItThrowsIllegalArgumentException(int value) {
        // given
        // when
        // then
        assertThatThrownBy(() -> EntryYear.of(value))
            .isInstanceOf(IllegalArgumentException.class);

      }
    }

    @Nested
    @DisplayName("범위 내 값에 대해서는")
    class ContextValidValue {

      @ParameterizedTest
      @ArgumentsSource(ValidValueArgumentsProvider.class)
      @DisplayName("객체를 생성한다.")
      void ItReturnInstance(int value) {
        // given
        // when
        EntryYear entryYear = EntryYear.of(value);

        // then
        assertThat(entryYear).isNotNull();
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
      @ArgumentsSource(ValidValueArgumentsProvider.class)
      @DisplayName("true를 반환한다")
      void ItReturnTrue(int value) {
        // given
        // when
        EntryYear entryYear1 = EntryYear.of(value);
        EntryYear entryYear2 = EntryYear.of(value);

        // then
        assertThat(entryYear1.equals(entryYear2)).isTrue();
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
        EntryYear entryYear1 = EntryYear.of(MIN_VALUE);
        EntryYear entryYear2 = EntryYear.of(MAX_VALUE);

        // then
        assertThat(entryYear1.equals(entryYear2)).isFalse();
      }
    }
  }

  static class OutOfRangeValueArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(Integer.MIN_VALUE),
          Arguments.of(MIN_VALUE - 1),
          Arguments.of(MAX_VALUE + 1),
          Arguments.of(Integer.MAX_VALUE)
      );
    }
  }

  static class ValidValueArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<Arguments> provideArguments(ExtensionContext context) {
      return Stream.of(
          Arguments.of(MIN_VALUE),
          Arguments.of(MAX_VALUE),
          Arguments.of((MIN_VALUE + MAX_VALUE) / 2)
      );
    }
  }

}
