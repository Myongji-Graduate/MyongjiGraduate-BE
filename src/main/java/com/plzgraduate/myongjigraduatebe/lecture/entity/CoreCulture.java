package com.plzgraduate.myongjigraduatebe.lecture.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoreCulture extends BaseEntity {

  @Column()
  private boolean mandatory;

  @Column(columnDefinition = "TINYINT UNSIGNED", nullable = false)
  private int startedEntryYear;

  @Column(columnDefinition = "TINYINT UNSIGNED")
  private int endedEntryYear;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey=@ForeignKey(name="FK_LECTURE_CORE_CULTURE"))
  private Lecture lecture;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey=@ForeignKey(name="FK_CULTURE_CATEGORY_CORE_CULTURE"))
  private CultureCategory cultureCategory;
}
