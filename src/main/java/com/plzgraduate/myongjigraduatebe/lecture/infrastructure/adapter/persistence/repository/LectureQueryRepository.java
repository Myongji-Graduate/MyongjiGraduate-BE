package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QLectureJpaEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LectureQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private static final QLectureJpaEntity lecture = QLectureJpaEntity.lectureJpaEntity;

	public List<LectureJpaEntity> searchByNameOrCode(String type, String keyword) {
		return jpaQueryFactory.selectFrom(lecture)
			.where(equalsType(type, keyword))
			.fetch();
	}

	private BooleanExpression equalsType(String type, String keyword) {
		if (type.equals("code")) {
			return lecture.lectureCode.contains(keyword);
		}
		return lecture.name.contains(keyword);
	}
}
