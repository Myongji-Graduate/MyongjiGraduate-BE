package com.plzgraduate.myongjigraduatebe.graduation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;
import com.plzgraduate.myongjigraduatebe.department.entity.Department;
import com.plzgraduate.myongjigraduatebe.graduation.dto.Transcript;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GraduationRequirement extends BaseEntity {

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int entryYear;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int totalCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int majorCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int commonCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int coreCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int basicAcademicalCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int normalCultureCredit;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int freeElectiveCredit;

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
      Department department
  ) {
    this.entryYear = entryYear;
    this.totalCredit = totalCredit;
    this.majorCredit = majorCredit;
    this.commonCultureCredit = commonCultureCredit;
    this.coreCultureCredit = coreCultureCredit;
    this.basicAcademicalCultureCredit = basicAcademicalCultureCredit;
    this.normalCultureCredit = normalCultureCredit;
    this.freeElectiveCredit = freeElectiveCredit;
    this.department = department;
  }

  public boolean checkCredit(Transcript transcript) {
    if (transcript.getTotalCredit() < totalCredit) {
      return false;
    }

    if (transcript.getMajorCredit() < majorCredit) {
      return false;
    }

    if (transcript.getCommonCultureCredit() < commonCultureCredit) {
      return false;
    }

    if (transcript.getCoreCultureCredit() < coreCultureCredit) {
      return false;
    }

    if (transcript.getBasicAcademicalCultureCredit() < basicAcademicalCultureCredit) {
      return false;
    }
    int normalCultureCredit = transcript.getNormalCultureCredit();
    normalCultureCredit += commonCultureCredit - transcript.getCommonCultureCredit();
    normalCultureCredit += coreCultureCredit - transcript.getCoreCultureCredit();
    normalCultureCredit += basicAcademicalCultureCredit - transcript.getBasicAcademicalCultureCredit();
    if (normalCultureCredit < this.normalCultureCredit) {
      return false;
    }

    int freeElectiveCredit = transcript.getFreeElectiveCredit();
    freeElectiveCredit += normalCultureCredit - this.normalCultureCredit;
    freeElectiveCredit += transcript.getMajorCredit() - majorCredit;

    return freeElectiveCredit >= this.freeElectiveCredit;
  }

  public int getTotalCategoryCredit(LectureCategory category) {
    switch (category) {
      case MAJOR:
        return getMajorCredit();
      case CORE_CULTURE:
        return getCoreCultureCredit();
      case COMMON_CULTURE:
        return getCommonCultureCredit();
      case BASIC_ACADEMICAL_CULTURE:
        return getBasicAcademicalCultureCredit();
      default:
        return category.getTotalCredit();
    }
  }
}