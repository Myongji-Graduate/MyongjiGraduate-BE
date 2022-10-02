package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "UNSIGNED TINYINT", nullable = false)
  private int credit;

  @Embedded
  private LectureCode lectureCode;

  public Lecture(
      String name,
      int credit,
      String lectureCode
  ) {
    this.name = name;
    this.credit = credit;
    this.lectureCode = new LectureCode(lectureCode);
  }
}
