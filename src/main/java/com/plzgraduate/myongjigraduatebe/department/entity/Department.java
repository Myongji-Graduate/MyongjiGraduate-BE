package com.plzgraduate.myongjigraduatebe.department.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department extends BaseEntity {

  public static final int DEFAULT_ENDED_ENTRY_YEAR = 99;
  @NotNull
  @Column()
  private String name;

  @NotNull
  @Positive
  @Column(columnDefinition = "UNSIGNED TINYINT", nullable = false)
  private int startedEntryYear;

  @NotNull
  @Positive
  @Column(columnDefinition = "UNSIGNED TINYINT")
  private int endedEntryYear;

  public Department(
      String name,
      int startedEntryYear
  ) {
    this(name, startedEntryYear, DEFAULT_ENDED_ENTRY_YEAR);
  }

  public Department(
      String name,
      int startedEntryYear,
      int endedEntryYear
  ) {
    this.name = name;
    this.startedEntryYear = startedEntryYear;
    this.endedEntryYear = endedEntryYear;
  }

  public boolean isSupported(int entryYear) {
    return startedEntryYear <= entryYear && entryYear <= endedEntryYear;
  }
}
