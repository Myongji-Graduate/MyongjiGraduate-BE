package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BasicAcademicalCulture extends BaseEntity {

  @Column()
  private boolean mandatory;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int startedEntryYear;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int endedEntryYear;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey=@ForeignKey(name="FK_LECTURE_BASIC_ACADEMICAL_CULTURE"))
  private Lecture lecture;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey=@ForeignKey(name="FK_DEPARTMENT_BASIC_ACADEMICAL_CULTURE"))
  private Department department;
}
