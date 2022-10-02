package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CultureCategory extends BaseEntity {

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int totalCredit;
}
