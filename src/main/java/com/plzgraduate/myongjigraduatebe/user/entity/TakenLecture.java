package com.plzgraduate.myongjigraduatebe.user.entity;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TakenLecture extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_USER_TAKEN_LECTURE"))
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(foreignKey = @ForeignKey(name = "FK_LECTURE_TAKEN_LECTURE"))
  private Lecture lecture;

  public TakenLecture(
      User user,
      Lecture lecture
  ) {
    this.user = user;
    this.lecture = lecture;
  }
}
