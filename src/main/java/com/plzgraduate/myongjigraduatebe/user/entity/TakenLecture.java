package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Objects;

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

  private String takenYear;

  private String takenSemester;

  public TakenLecture(
      User user,
      Lecture lecture,
      String takenYear,
      String takenSemester
  ) {
    this.user = user;
    this.lecture = lecture;
    this.takenYear = takenYear;
    this.takenSemester = takenSemester;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TakenLecture that = (TakenLecture)o;
    return Objects.equals(user, that.user) && Objects.equals(lecture, that.lecture)
        && Objects.equals(takenYear, that.takenYear) && Objects.equals(
        takenSemester,
        that.takenSemester
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, lecture, takenYear, takenSemester);
  }
}
