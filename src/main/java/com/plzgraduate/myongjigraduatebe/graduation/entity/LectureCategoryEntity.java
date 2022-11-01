package com.plzgraduate.myongjigraduatebe.graduation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LectureCategoryEntity extends BaseEntity {

  private String category;

  private String detailCategory;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int totalCredit;

}
