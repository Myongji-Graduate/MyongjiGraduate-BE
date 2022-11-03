package com.plzgraduate.myongjigraduatebe.user.entity;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;
import com.plzgraduate.myongjigraduatebe.common.entity.EntryYear;
import com.plzgraduate.myongjigraduatebe.common.entity.EntryYearConverter;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

  @Convert(converter = UserIdConverter.class)
  @Column(columnDefinition = "varchar(255)", unique = true, nullable = false)
  private UserId userId;

  @Column(columnDefinition = "char(60)")
  private String password;

  @Convert(converter = StudentNumberConverter.class)
  @Column(columnDefinition = "char(8)", unique = true, nullable = false)
  private StudentNumber studentNumber;

  @Convert(converter = EntryYearConverter.class)
  @Column(columnDefinition = "TINYINT UNSIGNED")
  private EntryYear entryYear;

  private String name;

  @Enumerated(value = EnumType.STRING)
  private EnglishLevel engLv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_DEPARTMENT_USER"))
  private Department department;

}
