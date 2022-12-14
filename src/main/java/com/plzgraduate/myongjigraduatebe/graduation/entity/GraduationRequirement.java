package com.plzgraduate.myongjigraduatebe.graduation.entity;

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
import com.plzgraduate.myongjigraduatebe.user.entity.EnglishLevel;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GraduationRequirement extends BaseEntity {

  @Convert(converter = EntryYearConverter.class)
  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private EntryYear entryYear;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int totalCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int majorCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int commonCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int coreCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int basicAcademicalCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int normalCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int freeElectiveCredit;

  @Enumerated(EnumType.STRING)
  private EnglishLevel engLv;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_DEPARTMENT_GRADUATION_REQUIREMENT"))
  private Department department;

  @Builder
  private GraduationRequirement(
      int entryYear,
      int totalCredit,
      int majorCredit,
      int commonCultureCredit,
      int coreCultureCredit,
      int basicAcademicalCultureCredit,
      int normalCultureCredit,
      int freeElectiveCredit,
      EnglishLevel engLv,
      Department department
  ) {
    this.entryYear = EntryYear.of(entryYear);
    this.totalCredit = totalCredit;
    this.majorCredit = majorCredit;
    this.commonCultureCredit = commonCultureCredit;
    this.coreCultureCredit = coreCultureCredit;
    this.basicAcademicalCultureCredit = basicAcademicalCultureCredit;
    this.normalCultureCredit = normalCultureCredit;
    this.freeElectiveCredit = freeElectiveCredit;
    this.engLv = engLv;
    this.department = department;
  }

  public int getCategoricalTotalCredit(GraduationCategory category) {
    switch (category) {
      case COMMON_CULTURE:
        return commonCultureCredit;
      case CORE_CULTURE:
        return coreCultureCredit;
      case BASIC_ACADEMICAL_CULTURE:
        return basicAcademicalCultureCredit;
      case MAJOR:
        return majorCredit;
      case NORMAL_CULTURE:
        return normalCultureCredit;
      case FREE_ELECTIVE:
        return freeElectiveCredit;
      default:
        return totalCredit;
    }
  }
}
