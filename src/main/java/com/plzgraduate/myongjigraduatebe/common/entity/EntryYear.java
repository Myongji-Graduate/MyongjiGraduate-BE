package com.plzgraduate.myongjigraduatebe.common.entity;

import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EntryYear {

  private static final int MIN_VALUE = 0;
  private static final int MAX_VALUE = 99;

  private int value;

  private EntryYear(int value) {
    this.value = value;
  }

  public static EntryYear of(int value) {
    validate(value);
    return new EntryYear(value);
  }

  private static void validate(int value) {
    if (value < MIN_VALUE || MAX_VALUE < value) {
      throw new IllegalArgumentException("학번을 확인해주세요");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    EntryYear entryYear = (EntryYear)o;
    return value == entryYear.value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}
