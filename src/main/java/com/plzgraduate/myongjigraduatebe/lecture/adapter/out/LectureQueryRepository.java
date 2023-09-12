package com.plzgraduate.myongjigraduatebe.lecture.adapter.out;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class LectureQueryRepository {

	private final JPAQueryFactory jpaQueryFactory;
	private final QLectureJpaEntity lecture = QLectureJpaEntity.lectureJpaEntity;

	public List<LectureJpaEntity> searchByNameOrCode(String type, String keyword) {
		return jpaQueryFactory.selectFrom(lecture)
			.where(equalsType(type, keyword))
			.fetch();
	}

	private BooleanExpression equalsType(String type, String keyword) {
		if(type.equals("name")) {
			return lecture.name.contains(keyword);
		} else if (type.equals("code")) {
			return lecture.lectureCode.contains(keyword);
		}
		return null;
	}
}
