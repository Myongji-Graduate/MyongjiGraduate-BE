package com.plzgraduate.myongjigraduatebe.graduation.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.plzgraduate.myongjigraduatebe.common.entity.BaseEntity;
import com.plzgraduate.myongjigraduatebe.lecture.entity.Lecture;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GraduationLecture extends BaseEntity {

  @Column(nullable = false)
  private boolean mandatory;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_LECTURE_GRADUATION_LECTURE"))
  private Lecture lecture;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_GRADUATION_REQUIREMENT_GRADUATION_LECTURE"))
  private GraduationRequirement graduationRequirement;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_LECTURE_CATEGORY_GRADUATION_LECTURE"))
  private LectureCategory lectureCategory;
}
