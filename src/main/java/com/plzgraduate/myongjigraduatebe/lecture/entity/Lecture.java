package com.plzgraduate.myongjigraduatebe.lecture.entity;

import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int credit;

  @Convert(converter = LectureCodeConverter.class)
  @Column(unique = true, updatable = false, columnDefinition = "VARCHAR(255)")
  private LectureCode lectureCode;

  @Convert(converter = LectureCodeConverter.class)
  @Column(columnDefinition = "VARCHAR(255)")
  private LectureCode duplicatedLectureCode;

  public Lecture(
      String name,
      int credit,
      String lectureCode
  ) {
    this.name = name;
    this.credit = credit;
    this.lectureCode = LectureCode.of(lectureCode);
  }

  @Builder()
  private Lecture(
      String name,
      int credit,
      String lectureCode,
      String duplicatedLectureCode
  ) {
    this.name = name;
    this.credit = credit;
    this.lectureCode = LectureCode.of(lectureCode);
    this.duplicatedLectureCode = LectureCode.of(duplicatedLectureCode);
  }

  public String getCode() {
    return lectureCode.getCode();
  }

  public Optional<String> getDuplicatedCode() {
    return duplicatedLectureCode == null ? Optional.empty() : Optional.of(duplicatedLectureCode.getCode());
  }
}
