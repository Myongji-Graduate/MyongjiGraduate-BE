package com.plzgraduate.myongjigraduatebe.user.entity;

import java.util.Objects;

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

  @Convert(converter = YearConverter.class)
  @Column(columnDefinition = "char(8)")
  private Year year;

  @Enumerated(value = EnumType.STRING)
  private Semester semester;

  public TakenLecture(
      User user,
      Lecture lecture,
      Year year,
      Semester semester
  ) {
    this.user = user;
    this.lecture = lecture;
    this.year = year;
    this.semester = semester;
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
    return Objects.equals(user, that.user) && Objects.equals(lecture, that.lecture);
  }

  @Override
  public int hashCode() {
    return Objects.hash(user, lecture);
  }
}
