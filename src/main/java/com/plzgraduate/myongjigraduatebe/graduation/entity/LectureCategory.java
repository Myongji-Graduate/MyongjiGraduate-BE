package com.plzgraduate.myongjigraduatebe.graduation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureCategory extends BaseEntity {

  @Enumerated(EnumType.STRING)
  private GraduationCategory category;

  private String detailCategory;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int totalCredit;

}
