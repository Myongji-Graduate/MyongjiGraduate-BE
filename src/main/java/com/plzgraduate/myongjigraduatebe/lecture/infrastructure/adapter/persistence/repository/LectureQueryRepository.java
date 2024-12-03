package com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.repository;

import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.LectureJpaEntity;
import com.plzgraduate.myongjigraduatebe.lecture.infrastructure.adapter.persistence.entity.QLectureJpaEntity;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LectureQueryRepository {

	private static final QLectureJpaEntity lecture = QLectureJpaEntity.lectureJpaEntity;
	private final JPAQueryFactory jpaQueryFactory;

	public List<LectureJpaEntity> searchByNameOrCode(String type, String keyword) {
		return jpaQueryFactory.selectFrom(lecture)
			.where(equalsType(type, keyword))
			.fetch();
	}

	private BooleanExpression equalsType(String type, String keyword) {
		if (type.equals("code")) {
			return lecture.id.contains(keyword);
		}
		return lecture.name.contains(keyword);
	}
}
