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

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }

    if (o == this) {
      return true;
    }

    if (o.getClass() != getClass()) {
      return false;
    }

    TakenLecture tl = (TakenLecture)o;
    return (this
        .getUser()
        .equals(tl.getUser())
        && this.getLecture().equals(tl.getLecture()));
  }

  @Override
  public int hashCode() {
    int hash = 7;

    hash = 31 * hash + getId().hashCode();

    return hash;
  }
  
}
