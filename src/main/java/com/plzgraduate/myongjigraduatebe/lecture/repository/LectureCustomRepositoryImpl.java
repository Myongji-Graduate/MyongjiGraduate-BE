package com.plzgraduate.myongjigraduatebe.lecture.repository;

import static com.plzgraduate.myongjigraduatebe.lecture.entity.QLecture.*;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.plzgraduate.myongjigraduatebe.lecture.dto.QSearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.dto.SearchedLecture;
import com.plzgraduate.myongjigraduatebe.lecture.entity.LectureCode;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LectureCustomRepositoryImpl implements LectureCustomRepository {

  private final JPAQueryFactory jpaQueryFactory;

  @Override
  public List<SearchedLecture> findByLectureCodeLike(String lectureCode) {

    return jpaQueryFactory
        .select(new QSearchedLecture(lecture.id, lecture.lectureCode, lecture.name, lecture.credit))
        .from(lecture)
        .where(likeLectureCode(lectureCode))
        .fetch();
  }

  @Override
  public List<SearchedLecture> findByLectureNameBy(String lectureName) {

    return jpaQueryFactory
        .select(new QSearchedLecture(lecture.id, lecture.lectureCode, lecture.name, lecture.credit))
        .from(lecture)
        .where(likeLectureName(lectureName))
        .fetch();
  }

  private BooleanExpression likeLectureCode(String lectureCode) {
    if (StringUtils.hasText(lectureCode)) {
      return lecture.lectureCode.eq(LectureCode.of(lectureCode));
    }
    return null;
  }

  private BooleanExpression likeLectureName(String lectureName) {
    if (StringUtils.hasText(lectureName)) {
      return lecture.name.like("%" + lectureName + "%");
    }
    return null;
  }
}
