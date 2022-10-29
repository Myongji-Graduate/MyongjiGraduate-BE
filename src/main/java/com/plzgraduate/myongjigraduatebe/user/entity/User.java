package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
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
public class User extends BaseEntity {

  @Convert(converter = StudentNumberConverter.class)
  @Column(columnDefinition = "char(8)", unique = true, nullable = false)
  private StudentNumber studentNumber;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int entryYear;

  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_DEPARTMENT_USER"))
  private Department department;

}
